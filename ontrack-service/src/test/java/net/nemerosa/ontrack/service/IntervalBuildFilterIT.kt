package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import org.junit.Test

class IntervalBuildFilterIT : AbstractBuildFilterIT() {

    protected fun filter(data: BuildIntervalFilterData): BuildFilterProviderData<BuildIntervalFilterData> {
        return buildFilterService.getBuildFilterProviderData(
                BuildIntervalFilterProvider::class.java.name,
                data
        )
    }

    @Test(expected = BuildNotFoundException::class)
    fun from_does_not_exist() {
        filter(BuildIntervalFilterData.of("xxx", null)).filterBranchBuilds(branch)
    }

    @Test(expected = BuildNotFoundException::class)
    fun to_does_not_exist() {
        build("1.0.0")
        filter(BuildIntervalFilterData.of("1.0.0", "xxx")).filterBranchBuilds(branch)
    }

    @Test
    fun from_only() {
        build("1.0.0")
        build("1.0.1")
        build("1.1.0")
        val builds = filter(BuildIntervalFilterData.of("1.0.1", null)).filterBranchBuilds(branch)
        checkList(builds, "1.1.0", "1.0.1")
    }

    @Test
    fun from_and_to() {
        build("1.0.0")
        build("1.0.1")
        build("1.0.2")
        build("1.1.0")
        val builds = filter(BuildIntervalFilterData.of("1.0.0", "1.0.2")).filterBranchBuilds(branch)
        checkList(builds, "1.0.2", "1.0.1", "1.0.0")
    }

    @Test
    fun from_and_to_reverse() {
        build("1.0.0")
        build("1.0.1")
        build("1.0.2")
        build("1.1.0")
        val builds = filter(BuildIntervalFilterData.of("1.0.2", "1.0.0")).filterBranchBuilds(branch)
        checkList(builds, "1.0.2", "1.0.1", "1.0.0")
    }

}