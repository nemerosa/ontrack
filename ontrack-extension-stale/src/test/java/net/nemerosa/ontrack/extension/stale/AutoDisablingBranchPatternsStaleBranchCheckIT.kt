package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.model.structure.Project
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AutoDisablingBranchPatternsStaleBranchCheckIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var staleJobService: StaleJobService

    @Autowired
    private lateinit var staleTestSupport: StaleTestSupport

    @Test
    fun `Project not configured, branches are not checked`() {
        project {
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Project configured with no pattern at all`() {
        project {
            autoDisablingBranchPatterns()
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Project configured with a pattern non-matching`() {
        project {
            autoDisablingBranchPatterns(
                items = listOf(
                    autoDisablingBranchPattern(
                        includes = listOf("release-.*"),
                    )
                )
            )
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Project configured with a pattern matching and using keep mode`() {
        project {
            autoDisablingBranchPatterns(
                items = listOf(
                    autoDisablingBranchPattern(
                        includes = listOf("release-.*"),
                    )
                )
            )
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Project configured with a pattern matching and using disable mode`() {
        project {
            autoDisablingBranchPatterns(
                items = listOf(
                    autoDisablingBranchPattern(
                        includes = listOf("release-.*"),
                        mode = AutoDisablingBranchPatternsMode.DISABLE,
                    )
                )
            )
            branch("release-1.2") {
                build {
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertTrue(it.isDisabled, "Branch has been disabled")
                }
            }
        }
    }

    @Test
    fun `First pattern is taken into account`() {
        project {
            autoDisablingBranchPatterns(
                items = listOf(
                    autoDisablingBranchPattern(
                        includes = listOf("develop"),
                        mode = AutoDisablingBranchPatternsMode.KEEP,
                    ),
                    autoDisablingBranchPattern(
                        includes = listOf("develop", "release-.*"),
                        mode = AutoDisablingBranchPatternsMode.DISABLE,
                    ),
                )
            )
            branch("develop") {
                build {
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Keeping the last 2 branches`() {
        project {
            autoDisablingBranchPatterns(
                items = listOf(
                    autoDisablingBranchPattern(
                        includes = listOf("release-.*"),
                        mode = AutoDisablingBranchPatternsMode.KEEP_LAST,
                    ),
                )
            )
            val branches = (0..4).map { no ->
                branch("release-1.$no")
            }
            staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
            (0..2).forEach { no ->
                val branch = branches[no]
                assertNotNull(structureService.findBranchByID(branch.id), "Branch has not been deleted") {
                    assertTrue(it.isDisabled, "Branch has been disabled")
                }
            }
            (3..4).forEach { no ->
                val branch = branches[no]
                assertNotNull(structureService.findBranchByID(branch.id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Combining the auto-disabling with branch patterns and stale branch`() {
        val now = Time.now
        project {
            autoDisablingBranchPatterns(
                items = listOf(
                    autoDisablingBranchPattern(
                        includes = listOf("release-.*"),
                        mode = AutoDisablingBranchPatternsMode.KEEP_LAST,
                        keepLast = 1,
                    ),
                )
            )
            staleTestSupport.staleBranches(
                project = this,
                disabling = 25,
                deleting = 55,
                excludes = "main",
            )
            val branches = (0..4).map { no ->
                branch("release-1.$no") {
                    build {
                        // 0 - 120 days ago - will be deleted
                        // 1 - 90 days ago - will be deleted
                        // ----- deletion cut off
                        // 2 - 60 days ago - will be kept and will be disabled
                        // 3 - 30 days ago - will be kept and will be disabled
                        // ----- disabling cut off
                        // 4 - now - will be kept and will be enabled
                        updateBuildSignature(time = now.minusDays(120L - no * 30))
                    }
                }
            }
            staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
            // Running it twice to make sure newly disabled branches are processed correctly
            staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)

            assertNull(structureService.findBranchByID(branches[0].id), "First branch has been deleted")

            assertNull(structureService.findBranchByID(branches[1].id), "Second branch has been deleted")

            assertNotNull(structureService.findBranchByID(branches[2].id), "Third branch has not been deleted") {
                assertTrue(it.isDisabled, "Third branch has been disabled")
            }

            assertNotNull(structureService.findBranchByID(branches[3].id), "Fourth branch has not been deleted") {
                assertTrue(it.isDisabled, "Fourth branch has been disabled")
            }

            assertNotNull(structureService.findBranchByID(branches[4].id), "Fifth branch has not been deleted") {
                assertFalse(it.isDisabled, "Fifth branch has not been disabled")
            }

        }
    }

    private fun Project.autoDisablingBranchPatterns(
        items: List<AutoDisablingBranchPatternsPropertyItem> = emptyList(),
    ) {
        setProperty(
            this,
            AutoDisablingBranchPatternsPropertyType::class.java,
            AutoDisablingBranchPatternsProperty(
                items = items,
            )
        )
    }

    private fun autoDisablingBranchPattern(
        includes: List<String> = emptyList(),
        mode: AutoDisablingBranchPatternsMode = AutoDisablingBranchPatternsMode.KEEP,
        keepLast: Int = 2,
    ) = AutoDisablingBranchPatternsPropertyItem(
        includes = includes,
        excludes = emptyList(),
        mode = mode,
        keepLast = keepLast,
    )

}