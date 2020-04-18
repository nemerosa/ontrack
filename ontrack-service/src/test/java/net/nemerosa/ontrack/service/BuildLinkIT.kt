package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.security.BuildConfig
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.security.BuildEdit
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildLinkIT : AbstractDSLTestSupport() {

    @Test(expected = ProjectNotFoundException::class)
    fun `Edition of links - project not found at all`() {
        val source = doCreateBuild()
        asUser().with(source, BuildConfig::class.java).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    BuildLinkForm(false,
                            BuildLinkFormItem(uid("P"), "xxx")
                    )
            )
        }
    }

    @Test(expected = ProjectNotFoundException::class)
    fun `Edition of links - project not authorised`() {
        withNoGrantViewToAll {
            val source = doCreateBuild()
            val target = doCreateBuild()
            asUser().with(source, BuildConfig::class.java).call {
                // Adds the link using a form
                structureService.editBuildLinks(
                        source,
                        BuildLinkForm(false,
                                BuildLinkFormItem(target.project.name, target.name)
                        )
                )
            }
        }
    }

    @Test(expected = BuildNotFoundException::class)
    fun `Edition of links - build not found`() {
        val source = doCreateBuild()
        val target = doCreateProject()
        asUser().with(source, BuildConfig::class.java).withView(target).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    BuildLinkForm(false,
                            BuildLinkFormItem(target.name, "xxx")
                    )
            )
        }
    }

    @Test
    fun `Edition of links - full rights - adding one link`() {
        val source = doCreateBuild()
        val target1 = doCreateBuild()
        val target2 = doCreateBuild()
        val target3 = doCreateBuild()
        asUser().with(source, BuildConfig::class.java).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig::class.java).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig::class.java).withView(target1).withView(target2).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    BuildLinkForm(false,
                            BuildLinkFormItem(target1.project.name, target1.name), // Existing
                            BuildLinkFormItem(target2.project.name, target2.name), // Existing
                            BuildLinkFormItem(target3.project.name, target3.name) // New
                    )
            )
            // Checks all builds are still linked
            assertEquals(
                    setOf(target1.id, target2.id, target3.id),
                    structureService.getBuildsUsedBy(source).pageItems.map { it.id }.toSet()
            )
        }
    }

    @Test
    fun `Edition of links - full rights - adding and removing`() {
        val source = doCreateBuild()
        val target1 = doCreateBuild()
        val target2 = doCreateBuild()
        val target3 = doCreateBuild()
        asUser().with(source, BuildConfig::class.java).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig::class.java).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig::class.java).withView(target1).withView(target2).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    BuildLinkForm(false,
                            BuildLinkFormItem(target1.project.name, target1.name), // Existing
                            // BuildLinkFormItem(target2.project.name, target2.name), // Removing
                            BuildLinkFormItem(target3.project.name, target3.name) // New
                    )
            )
            // Checks all builds are still linked
            assertEquals(
                    setOf(target1.id, target3.id),
                    structureService.getBuildsUsedBy(source).pageItems.map { it.id }.toSet()
            )
        }
    }

    @Test
    fun `Edition of links - full rights - adding only`() {
        val source = doCreateBuild()
        val target1 = doCreateBuild()
        val target2 = doCreateBuild()
        val target3 = doCreateBuild()
        asUser().with(source, BuildConfig::class.java).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig::class.java).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig::class.java).withView(target1).withView(target2).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    BuildLinkForm(true,
                            BuildLinkFormItem(target3.project.name, target3.name) // New
                    )
            )
            // Checks all builds are still linked
            assertEquals(
                    setOf(target1.id, target2.id, target3.id),
                    structureService.getBuildsUsedBy(source).pageItems.map { it.id }.toSet()
            )
        }
    }

    @Test
    fun `Edition of links - partial rights - adding one link`() {
        withNoGrantViewToAll {
            val source = doCreateBuild()
            val target1 = doCreateBuild()
            val target2 = doCreateBuild()
            val target3 = doCreateBuild()
            asUser().with(source, BuildConfig::class.java).withView(target1).call {
                structureService.addBuildLink(source, target1)
            }
            asUser().with(source, BuildConfig::class.java).withView(target2).call {
                structureService.addBuildLink(source, target2)
            }
            asUser().with(source, BuildConfig::class.java).withView(target1).withView(target3).call {
                // Adds the link using a form
                structureService.editBuildLinks(
                        source,
                        BuildLinkForm(false,
                                BuildLinkFormItem(target1.project.name, target1.name), // Existing
                                BuildLinkFormItem(target3.project.name, target3.name) // New
                        )
                )
            }
            asUser().with(source, BuildConfig::class.java).withView(target1).withView(target2).withView(target3).call {
                // Checks all builds are still linked
                assertEquals(
                        setOf(target1.id, target2.id, target3.id),
                        structureService.getBuildsUsedBy(source).pageItems.map { it.id }.toSet()
                )
            }
        }
    }

    @Test
    fun `Edition of links - partial rights - adding and removing`() {
        withNoGrantViewToAll {
            val source = doCreateBuild()
            val target1 = doCreateBuild()
            val target2 = doCreateBuild()
            val target3 = doCreateBuild()
            asUser().with(source, BuildConfig::class.java).withView(target1).call {
                structureService.addBuildLink(source, target1)
            }
            asUser().with(source, BuildConfig::class.java).withView(target2).call {
                structureService.addBuildLink(source, target2)
            }
            asUser().with(source, BuildConfig::class.java).withView(target1).withView(target3).call {
                // Adds the link using a form
                structureService.editBuildLinks(
                        source,
                        BuildLinkForm(false,
                                // BuildLinkFormItem(target1.project.name, target1.name), // Removing
                                BuildLinkFormItem(target3.project.name, target3.name) // New
                        )
                )
            }
            asUser().with(source, BuildConfig::class.java).withView(target1).withView(target2).withView(target3).call {
                // Checks all builds are still linked
                assertEquals(
                        setOf(target2.id, target3.id),
                        structureService.getBuildsUsedBy(source).pageItems.map { it.id }.toSet()
                )
            }
        }
    }

    @Test
    fun `Edition of links - partial rights - adding only`() {
        val source = doCreateBuild()
        val target1 = doCreateBuild()
        val target2 = doCreateBuild()
        val target3 = doCreateBuild()
        asUser().with(source, BuildConfig::class.java).withView(target1).call {
            structureService.addBuildLink(source, target1)
        }
        asUser().with(source, BuildConfig::class.java).withView(target2).call {
            structureService.addBuildLink(source, target2)
        }
        asUser().with(source, BuildConfig::class.java).withView(target1).withView(target3).call {
            // Adds the link using a form
            structureService.editBuildLinks(
                    source,
                    BuildLinkForm(true,
                            BuildLinkFormItem(target3.project.name, target3.name) // New
                    )
            )
        }
        asUser().with(source, BuildConfig::class.java).withView(target1).withView(target2).withView(target3).call {
            // Checks all builds are still linked
            assertEquals(
                    setOf(target1.id, target2.id, target3.id),
                    structureService.getBuildsUsedBy(source).pageItems.map { it.id }.toSet()
            )
        }
    }

    @Test
    fun `Automation role can create links`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asGlobalRole("AUTOMATION").call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        val targets = asUser().withView(build).withView(target).call {
            structureService.getBuildsUsedBy(build)
        }.pageItems
        assertTrue(targets.isNotEmpty())
        assertTrue(targets.any { it.name == target.name })
    }

    @Test(expected = AccessDeniedException::class)
    fun `Build config is needed on source build to create a link`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asUser().withView(target).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `Build view is needed on target build to create a link`() {
        withNoGrantViewToAll {
            // Creates a build
            val build = doCreateBuild()
            // Creates a second build to link
            val target = doCreateBuild()
            // Build link creation
            asUser().with(build, BuildConfig::class.java).call {
                structureService.addBuildLink(build, target)
            }
        }
    }

    @Test
    fun `Adding and deleting a build`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig::class.java).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        var targets = asUser().withView(build).withView(target).call {
            structureService.getBuildsUsedBy(build)
        }.pageItems
        assertTrue(targets.isNotEmpty())
        assertTrue(targets.any { it.name == target.name })
        // Deleting the build
        asUser().with(build, BuildConfig::class.java).withView(target).call {
            structureService.deleteBuildLink(build, target)
        }
        // The build link is deleted
        targets = asUser().withView(build).withView(target).call {
            structureService.getBuildsUsedBy(build)
        }.pageItems
        assertTrue(targets.isEmpty())
    }

    @Test
    fun `Adding twice a build`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig::class.java).withView(target).call {
            structureService.addBuildLink(build, target)
            // ... twice
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        val targets = asUser().withView(build).withView(target).call {
            structureService.getBuildsUsedBy(build)
        }.pageItems
        assertTrue(targets.isNotEmpty())
        assertEquals(1, targets.size)
        assertTrue(targets.any { it.name == target.name })
    }

    @Test
    fun `Controller role can create links`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asGlobalRole("CONTROLLER").call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        val targets = asUser().withView(build).withView(target).call {
            structureService.getBuildsUsedBy(build)
        }.pageItems
        assertEquals(listOf(target.name), targets.map { it.name })
    }

    @Test(expected = AccessDeniedException::class)
    fun `Creator role cannot create links`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asGlobalRole("CREATOR").call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    fun `Build config function grants access to create links`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig::class.java).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        val targets = asUser().withView(build).withView(target).call {
            structureService.getBuildsUsedBy(build)
        }.pageItems
        assertEquals(listOf(target.name), targets.map { it.name })
    }

    @Test
    fun `Build edit function grants access to create links`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildEdit::class.java).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        val targets = asUser().withView(build).withView(target).call {
            structureService.getBuildsUsedBy(build)
        }.pageItems
        assertEquals(listOf(target.name), targets.map { it.name })
    }

    @Test(expected = AccessDeniedException::class)
    fun `Build create function does not grant access to create links`() {
        // Creates a build
        val build = doCreateBuild()
        // Creates a second build to link
        val target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildCreate::class.java).withView(target).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    fun `Testing the links`() {
        // Creates all builds
        val b1 = doCreateBuild()
        val b2 = doCreateBuild()
        val t1 = doCreateBuild()
        val t2 = doCreateBuild()
        // Creates the links
        asUser().with(b1, BuildConfig::class.java).withView(t1).withView(t2).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b1, t2)
        }
        asUser().with(b2, BuildConfig::class.java).withView(t2).call {
            structureService.addBuildLink(b2, t2)
        }
        // With full rights
        asUserWithView(b1, b2, t1, t2).call {
            assertTrue(structureService.isLinkedTo(b1, t1.project.name, ""))
            assertTrue(structureService.isLinkedTo(b1, t2.project.name, ""))
            assertTrue(structureService.isLinkedTo(b1, t1.project.name, t1.name))
            assertTrue(structureService.isLinkedTo(b1, t2.project.name, t2.name))
            assertTrue(structureService.isLinkedTo(b1, t1.project.name, t1.name.substring(0, 5) + "*"))
            assertTrue(structureService.isLinkedTo(b1, t2.project.name, t2.name.substring(0, 5) + "*"))

            assertTrue(structureService.isLinkedFrom(t2, b1.project.name, ""))
            assertTrue(structureService.isLinkedFrom(t2, b1.project.name, b1.name))
            assertTrue(structureService.isLinkedFrom(t2, b1.project.name, b1.name.substring(0, 5) + "*"))

            assertTrue(structureService.isLinkedFrom(t2, b2.project.name, ""))
            assertTrue(structureService.isLinkedFrom(t2, b2.project.name, b2.name))
            assertTrue(structureService.isLinkedFrom(t2, b2.project.name, b2.name.substring(0, 5) + "*"))
        }
    }

    @Test
    fun `Filter on linked to`() {
        // Source branch
        val source = doCreateBranch()
        val b1 = doCreateBuild(source, nd("1.0.0", ""))
        val b2 = doCreateBuild(source, nd("1.1.0", ""))
        // Target branch
        val target = doCreateBranch()
        val t1 = doCreateBuild(target, nd("2.0.0", ""))
        val t2 = doCreateBuild(target, nd("2.1.0", ""))
        // Creates links
        asUser().withView(target).with(source, BuildConfig::class.java).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertBuildLinkedToFilter(source, target, target.project.name, listOf(b2, b1))
        // Standard filter on project and all builds
        assertBuildLinkedToFilter(source, target, "${target.project.name}:", listOf(b2, b1))
        // Standard filter on project and all builds, using *
        assertBuildLinkedToFilter(source, target, "${target.project.name}:*", listOf(b2, b1))
        // Standard filter on project and common build prefix
        assertBuildLinkedToFilter(source, target, "${target.project.name}:2*", listOf(b2, b1))
        // Standard filter on project and prefix for one
        assertBuildLinkedToFilter(source, target, "${target.project.name}:2.0*", listOf(b1))
        // Standard filter on project and exact build
        assertBuildLinkedToFilter(source, target, "${target.project.name}:2.0.0", listOf(b1))
    }

    private fun assertBuildLinkedToFilter(source: Branch, target: Branch, pattern: String, expected: List<Build>) {
        asUserWithView(source, target).call {
            val builds = buildFilterService
                    .standardFilterProviderData(10)
                    .withLinkedTo(pattern)
                    .build()
                    .filterBranchBuilds(source)
            assertEquals(
                    expected.map { it.id },
                    builds.map { it.id }
            )
        }
    }

    @Test
    fun `Filter on linked from`() {
        // Source branch
        val source = doCreateBranch()
        val b1 = doCreateBuild(source, nd("1.0.0", ""))
        val b2 = doCreateBuild(source, nd("1.1.0", ""))
        // Target branch
        val target = doCreateBranch()
        val t1 = doCreateBuild(target, nd("2.0.0", ""))
        val t2 = doCreateBuild(target, nd("2.1.0", ""))
        // Creates links
        asUser().withView(target).with(source, BuildConfig::class.java).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertBuildLinkedFromFilter(source, target, source.project.name, listOf(t2, t1))
        // Standard filter on project and all builds
        assertBuildLinkedFromFilter(source, target, "${source.project.name}:", listOf(t2, t1))
        // Standard filter on project and all builds, using *
        assertBuildLinkedFromFilter(source, target, "${source.project.name}:*", listOf(t2, t1))
        // Standard filter on project and common build prefix
        assertBuildLinkedFromFilter(source, target, "${source.project.name}:1*", listOf(t2, t1))
        // Standard filter on project and prefix for one
        assertBuildLinkedFromFilter(source, target, "${source.project.name}:1.0*", listOf(t1))
        // Standard filter on project and exact build
        assertBuildLinkedFromFilter(source, target, "${source.project.name}:1.0.0", listOf(t1))
    }

    private fun assertBuildLinkedFromFilter(source: Branch, target: Branch, pattern: String, expected: List<Build>) {
        asUserWithView(source, target).call {
            val builds = buildFilterService
                    .standardFilterProviderData(10)
                    .withLinkedFrom(pattern)
                    .build()
                    .filterBranchBuilds(target)
            assertEquals(
                    expected.map { it.id },
                    builds.map { it.id }
            )
        }
    }

    @Test
    fun `Project search on build linked from`() {
        // Source branch
        val source = doCreateBranch()
        val b1 = doCreateBuild(source, nd("1.0.0", ""))
        val b2 = doCreateBuild(source, nd("1.1.0", ""))
        // Target branch
        val target = doCreateBranch()
        val t1 = doCreateBuild(target, nd("2.0.0", ""))
        val t2 = doCreateBuild(target, nd("2.1.0", ""))
        // Creates links
        asUser().withView(target).with(source, BuildConfig::class.java).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertProjectLinkedFromFilter(source, target, source.project.name, listOf(t2, t1))
        // Standard filter on project and all builds
        assertProjectLinkedFromFilter(source, target, "${source.project.name}:", listOf(t2, t1))
        // Standard filter on project and all builds, using *
        assertProjectLinkedFromFilter(source, target, "${source.project.name}:*", listOf(t2, t1))
        // Standard filter on project and common build prefix
        assertProjectLinkedFromFilter(source, target, "${source.project.name}:1*", listOf(t2, t1))
        // Standard filter on project and prefix for one
        assertProjectLinkedFromFilter(source, target, "${source.project.name}:1.0*", listOf(t1))
        // Standard filter on project and exact build
        assertProjectLinkedFromFilter(source, target, "${source.project.name}:1.0.0", listOf(t1))
    }

    private fun assertProjectLinkedFromFilter(source: Branch, target: Branch, pattern: String, expected: List<Build>) {
        asUserWithView(source, target).call {
            val builds = structureService.buildSearch(
                    target.project.id,
                    BuildSearchForm().withLinkedFrom(pattern)
            )
            assertEquals(
                    expected.map { it.id },
                    builds.map { it.id }
            )
        }
    }

    @Test
    fun `Project search on linked to`() {
        // Source branch
        val source = doCreateBranch()
        val b1 = doCreateBuild(source, nd("1.0.0", ""))
        val b2 = doCreateBuild(source, nd("1.1.0", ""))
        // Target branch
        val target = doCreateBranch()
        val t1 = doCreateBuild(target, nd("2.0.0", ""))
        val t2 = doCreateBuild(target, nd("2.1.0", ""))
        // Creates links
        asUser().withView(target).with(source, BuildConfig::class.java).call {
            structureService.addBuildLink(b1, t1)
            structureService.addBuildLink(b2, t2)
        }
        // Standard filter on project
        assertProjectLinkedToFilter(source, target, target.project.name, listOf(b2, b1))
        // Standard filter on project and all builds
        assertProjectLinkedToFilter(source, target, "${target.project.name}:", listOf(b2, b1))
        // Standard filter on project and all builds, using *
        assertProjectLinkedToFilter(source, target, "${target.project.name}:*", listOf(b2, b1))
        // Standard filter on project and common build prefix
        assertProjectLinkedToFilter(source, target, "${target.project.name}:2*", listOf(b2, b1))
        // Standard filter on project and prefix for one
        assertProjectLinkedToFilter(source, target, "${target.project.name}:2.0*", listOf(b1))
        // Standard filter on project and exact build
        assertProjectLinkedToFilter(source, target, "${target.project.name}:2.0.0", listOf(b1))
    }

    private fun assertProjectLinkedToFilter(source: Branch, target: Branch, pattern: String, expected: List<Build>) {
        asUserWithView(source, target).call {
            val builds = structureService.buildSearch(
                    source.project.id,
                    BuildSearchForm().withLinkedTo(pattern)
            )
            assertEquals(
                    expected.map { it.id },
                    builds.map { it.id }
            )
        }
    }

}
