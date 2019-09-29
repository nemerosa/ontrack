package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.structure.Build
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IntervalBuildFilterIT : AbstractDSLTestSupport() {

    protected fun filter(data: BuildIntervalFilterData): BuildFilterProviderData<BuildIntervalFilterData> {
        return buildFilterService.getBuildFilterProviderData(
                BuildIntervalFilterProvider::class.java.name,
                data
        )
    }

    @Test
    fun `Validates the from build`() {
        project {
            branch {
                build("1.0.1")
                val message = buildFilterService.validateBuildFilterProviderData(
                        this,
                        BuildIntervalFilterProvider::class.java.name,
                        mapOf(
                                "from" to "1.0.0",
                                "to" to "1.0.1"
                        ).toJson()
                )
                assertEquals("""Build "1.0.0" does not exist for "$entityDisplayName".""", message)
            }
        }
    }

    @Test
    fun `Validates the to build`() {
        project {
            branch {
                build("1.0.0")
                val message = buildFilterService.validateBuildFilterProviderData(
                        this,
                        BuildIntervalFilterProvider::class.java.name,
                        mapOf(
                                "from" to "1.0.0",
                                "to" to "1.0.1"
                        ).toJson()
                )
                assertEquals("""Build "1.0.1" does not exist for "$entityDisplayName".""", message)
            }
        }
    }

    @Test
    fun `Validates the builds`() {
        project {
            branch {
                build("1.0.0")
                build("1.0.1")
                val message = buildFilterService.validateBuildFilterProviderData(
                        this,
                        BuildIntervalFilterProvider::class.java.name,
                        mapOf(
                                "from" to "1.0.0",
                                "to" to "1.0.1"
                        ).toJson()
                )
                assertNull(message)
            }
        }
    }

    @Test
    fun from_does_not_exist() {
        project {
            branch {
                val builds = filter(BuildIntervalFilterData("xxx", null)).filterBranchBuilds(this)
                assertTrue(builds.isEmpty(), "No build is returned")
            }
        }
    }

    @Test
    fun to_does_not_exist() {
        project {
            branch {
                build("1.0.0")
                val builds = filter(BuildIntervalFilterData("1.0.0", "xxx")).filterBranchBuilds(this)
                assertTrue(builds.isEmpty(), "No build is returned")
            }
        }
    }

    @Test
    fun from_only() {
        project {
            branch {
                build("1.0.0")
                build("1.0.1")
                build("1.1.0")
                val builds = filter(BuildIntervalFilterData("1.0.1", null)).filterBranchBuilds(this)
                checkList(builds, "1.1.0", "1.0.1")
            }
        }
    }

    @Test
    fun from_and_to() {
        project {
            branch {
                build("1.0.0")
                build("1.0.1")
                build("1.0.2")
                build("1.1.0")
                val builds = filter(BuildIntervalFilterData("1.0.0", "1.0.2")).filterBranchBuilds(this)
                checkList(builds, "1.0.2", "1.0.1", "1.0.0")
            }
        }
    }

    @Test
    fun from_and_to_reverse() {
        project {
            branch {
                build("1.0.0")
                build("1.0.1")
                build("1.0.2")
                build("1.1.0")
                val builds = filter(BuildIntervalFilterData("1.0.2", "1.0.0")).filterBranchBuilds(this)
                checkList(builds, "1.0.2", "1.0.1", "1.0.0")
            }
        }
    }

    protected fun checkList(builds: List<Build>, vararg expectedNames: String) {
        val actualNames = builds
                .map { it.name }
        Assert.assertEquals(expectedNames.toList(), actualNames)
    }

}