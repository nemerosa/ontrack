package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IntervalBuildFilterIT : AbstractBuildFilterIT() {

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
        val builds = filter(BuildIntervalFilterData("xxx", null)).filterBranchBuilds(branch)
        assertTrue(builds.isEmpty(), "No build is returned")
    }

    @Test
    fun to_does_not_exist() {
        build("1.0.0")
        val builds = filter(BuildIntervalFilterData("1.0.0", "xxx")).filterBranchBuilds(branch)
        assertTrue(builds.isEmpty(), "No build is returned")
    }

    @Test
    fun from_only() {
        build("1.0.0")
        build("1.0.1")
        build("1.1.0")
        val builds = filter(BuildIntervalFilterData("1.0.1", null)).filterBranchBuilds(branch)
        checkList(builds, "1.1.0", "1.0.1")
    }

    @Test
    fun from_and_to() {
        build("1.0.0")
        build("1.0.1")
        build("1.0.2")
        build("1.1.0")
        val builds = filter(BuildIntervalFilterData("1.0.0", "1.0.2")).filterBranchBuilds(branch)
        checkList(builds, "1.0.2", "1.0.1", "1.0.0")
    }

    @Test
    fun from_and_to_reverse() {
        build("1.0.0")
        build("1.0.1")
        build("1.0.2")
        build("1.1.0")
        val builds = filter(BuildIntervalFilterData("1.0.2", "1.0.0")).filterBranchBuilds(branch)
        checkList(builds, "1.0.2", "1.0.1", "1.0.0")
    }

}