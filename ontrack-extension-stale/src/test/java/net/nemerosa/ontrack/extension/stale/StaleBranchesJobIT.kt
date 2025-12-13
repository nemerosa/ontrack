package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.job.JobRunListener
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@AsAdminTest
class StaleBranchesJobIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var staleJobService: StaleJobService

    @Autowired
    private lateinit var staleTestSupport: StaleTestSupport

    @Test
    fun `Deleting a branch using last build time`() {
        project {
            staleTestSupport.staleBranches(
                project = project,
                disabling = 5,
                deleting = 10
            )
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNull(structureService.findBranchByID(id), "Branch has been deleted")
            }
        }
    }

    @Test
    fun `Disabling only a branch using last build time when deletion time is not set`() {
        project {
            staleTestSupport.staleBranches(
                project = project,
                disabling = 5,
                deleting = null
            )
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(11))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertTrue(it.isDisabled, "Branch has been disabled")
                }
            }
        }
    }

    @Test
    fun `Disabling a branch using last build time`() {
        project {
            staleTestSupport.staleBranches(
                project = project,
                disabling = 5,
                deleting = 10
            )
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(6))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertTrue(it.isDisabled, "Branch has been disabled")
                }
            }
        }
    }

    @Test
    fun `Not touching a branch using last build time`() {
        project {
            staleTestSupport.staleBranches(
                project = project,
                disabling = 5,
                deleting = 10
            )
            branch {
                build {
                    updateBuildSignature(time = Time.now().minusDays(4))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has been disabled")
                }
            }
        }
    }

    @Test
    fun `Deleting a branch using branch creation time`() {
        project {
            staleTestSupport.staleBranches(
                project = project,
                disabling = 5,
                deleting = 10
            )
            branch {
                updateBranchSignature(time = Time.now().minusDays(16))
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNull(structureService.findBranchByID(id), "Branch has been deleted")
            }
        }
    }

    @Test
    fun `Disabling a branch using branch creation time`() {
        project {
            staleTestSupport.staleBranches(
                project = project,
                disabling = 5,
                deleting = 10
            )
            branch {
                updateBranchSignature(time = Time.now().minusDays(6))
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertTrue(it.isDisabled, "Branch has been disabled")
                }
            }
        }
    }

    @Test
    fun `Not disabling a branch because of promotions`() {
        project {
            branch {
                updateBranchSignature(time = Time.now().minusDays(7))
                val pl = promotionLevel()
                staleTestSupport.staleBranches(
                    project = this@project.project,
                    disabling = 5,
                    deleting = 10,
                    promotionsToKeep = listOf<String>(pl.name)
                )
                build {
                    promote(pl)
                    updateBuildSignature(time = Time.now().minusDays(6))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Disabling a branch even if promoted when not configured`() {
        project {
            branch {
                updateBranchSignature(time = Time.now().minusDays(7))
                val pl = promotionLevel()
                staleTestSupport.staleBranches(
                    project = this@project.project,
                    disabling = 5,
                    deleting = 10,
                    promotionsToKeep = null
                )
                build {
                    promote(pl)
                    updateBuildSignature(time = Time.now().minusDays(6))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertTrue(it.isDisabled, "Branch has been disabled")
                }
            }
        }
    }

    @Test
    fun `Not deleting a branch because of promotions`() {
        project {
            branch {
                updateBranchSignature(time = Time.now().minusDays(12))
                val pl = promotionLevel()
                staleTestSupport.staleBranches(
                    project = this@project.project,
                    disabling = 5,
                    deleting = 10,
                    promotionsToKeep = listOf<String>(pl.name)
                )
                build {
                    promote(pl)
                    updateBuildSignature(time = Time.now().minusDays(11))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Not deleting a branch because of includes rule`() {
        project {
            branch("release-1.0") {
                updateBranchSignature(time = Time.now().minusDays(20))
                val pl = promotionLevel()
                staleTestSupport.staleBranches(
                    project = this@project.project,
                    disabling = 5,
                    deleting = 10,
                    includes = "release-.*"
                )
                build {
                    promote(pl)
                    updateBuildSignature(time = Time.now().minusDays(18))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Not deleting a branch because of includes rule and excludes rule`() {
        project {
            branch("release-2.0") {
                updateBranchSignature(time = Time.now().minusDays(20))
                val pl = promotionLevel()
                staleTestSupport.staleBranches(
                    project = this@project.project,
                    disabling = 5,
                    deleting = 10,
                    includes = "release-.*",
                    excludes = "release-1.*"
                )
                build {
                    promote(pl)
                    updateBuildSignature(time = Time.now().minusDays(18))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

    @Test
    fun `Deleting a branch because of includes rule and excludes rule`() {
        project {
            branch("release-1.0") {
                updateBranchSignature(time = Time.now().minusDays(20))
                val pl = promotionLevel()
                staleTestSupport.staleBranches(
                    project = this@project.project,
                    disabling = 5,
                    deleting = 10,
                    includes = "release-.*",
                    excludes = "release-1.*"
                )
                build {
                    promote(pl)
                    updateBuildSignature(time = Time.now().minusDays(18))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNull(structureService.findBranchByID(id), "Branch has been deleted")
            }
        }
    }

    @Test
    fun `Deleting a branch even if promoted when not configured`() {
        project {
            branch {
                updateBranchSignature(time = Time.now().minusDays(6))
                val pl = promotionLevel()
                staleTestSupport.staleBranches(
                    project = this@project.project,
                    disabling = 5,
                    deleting = 10,
                    promotionsToKeep = null
                )
                build {
                    promote(pl)
                    updateBuildSignature(time = Time.now().minusDays(16))
                }
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNull(structureService.findBranchByID(id), "Branch has been deleted")
            }
        }
    }

    @Test
    fun `Not touching a branch using branch creation time`() {
        project {
            staleTestSupport.staleBranches(
                project = project,
                disabling = 5,
                deleting = 10
            )
            branch {
                updateBranchSignature(time = Time.now().minusDays(4))
                staleJobService.detectAndManageStaleBranches(JobRunListener.out(), project)
                assertNotNull(structureService.findBranchByID(id), "Branch has not been deleted") {
                    assertFalse(it.isDisabled, "Branch has not been disabled")
                }
            }
        }
    }

}
