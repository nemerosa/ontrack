package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class NamedBuildFilterIT : AbstractBuildFilterIT() {

    protected fun filter(data: NamedBuildFilterData?): BuildFilterProviderData<NamedBuildFilterData> {
        return buildFilterService.getBuildFilterProviderData(
                NamedBuildFilterProvider::class.java.name,
                data
        )
    }

    @Test
    @Throws(Exception::class)
    fun from_only_with_explicit_build() { 
        // Builds
        build("1.0.0")
        build("1.0.1")
        build("1.1.0")
        build("2.0.0")
        build("2.0.1")
        // Filter
        val builds = filter(NamedBuildFilterData.of("1.1.0")).filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, "2.0.1", "2.0.0", "1.1.0")
    }

    @Test
    @Throws(Exception::class)
    fun from_only_with_pattern() { // Builds
        build("1.0.0")
        build("1.0.1")
        build("1.1.0")
        build("2.0.0")
        build("2.0.1")
        // Filter
        val builds = filter(NamedBuildFilterData.of("1.0.*")).filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, "2.0.1", "2.0.0", "1.1.0", "1.0.1")
    }

    @Test
    @Throws(Exception::class)
    fun from_only_with_explicit_build_and_promotion_before_from() { // Builds
        build("1.0.0").withPromotion(bronze)
        build("1.0.1")
        build("1.1.0")
        build("2.0.0")
        build("2.0.1").withPromotion(bronze)
        // Filter
        val builds = filter(NamedBuildFilterData.of("1.1.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, "2.0.1")
    }

    @Test
    @Throws(Exception::class)
    fun from_only_with_explicit_build_and_promotion_after_from() { // Builds
        build("1.0.0")
        build("1.0.1")
        build("1.1.0").withPromotion(bronze)
        build("2.0.0")
        build("2.0.1").withPromotion(bronze)
        // Filter
        val builds = filter(NamedBuildFilterData.of("1.0.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, "2.0.1", "1.1.0")
    }

    @Test
    @Throws(Exception::class)
    fun from_only_with_explicit_build_and_promotion_on_from() { // Builds
        build("1.0.0")
        build("1.0.1")
        build("1.1.0").withPromotion(bronze)
        build("2.0.0")
        build("2.0.1").withPromotion(bronze)
        // Filter
        val builds = filter(NamedBuildFilterData.of("1.1.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, "2.0.1", "1.1.0")
    }

    @Test
    @Throws(Exception::class)
    fun from_only_with_pattern_and_promotions() { // Builds
        build("1.0.0")
        build("1.0.1").withPromotion(bronze)
        build("1.1.0")
        build("2.0.0").withPromotion(bronze)
        build("2.0.1")
        // Filter
        val builds = filter(NamedBuildFilterData.of("1.0.*").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, "2.0.0", "1.0.1")
    }

    @Test
    @Throws(Exception::class)
    fun no_from() { // Builds
        build("1.0.0")
        build("1.1.0")
        build("2.0.1")
        // Filter
        val builds = filter(NamedBuildFilterData.of("0.9.0")).filterBranchBuilds(branch)
        // Checks the list
        Assert.assertTrue(builds.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun no_from_with_pattern() { // Builds
        build("1.0.0")
        build("1.1.0")
        build("2.0.1")
        // Filter
        val builds = filter(NamedBuildFilterData.of("0.9.*")).filterBranchBuilds(branch)
        // Checks the list
        Assert.assertTrue(builds.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun from_and_since_with_no_promotion_criteria() { // Builds
        build("1.0.0")
        build("1.0.1")
        build("1.1.0")
        build("2.0.0")
        build("2.0.1")
        // From & since are explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.0")).filterBranchBuilds(branch),
                "2.0.0", "1.1.0")
        // From is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.*")).filterBranchBuilds(branch),
                "2.0.1", "2.0.0", "1.1.0")
        // Since is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.0")).filterBranchBuilds(branch),
                "2.0.0", "1.1.0", "1.0.1")
        // None is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.*")).filterBranchBuilds(branch),
                "2.0.1", "2.0.0", "1.1.0", "1.0.1")
    }

    @Test
    @Throws(Exception::class)
    fun from_and_since_with_promotion() { // Builds
        build("1.0.0")
        build("1.0.1").withPromotion(bronze)
        build("1.1.0")
        build("2.0.0").withPromotion(bronze)
        build("2.0.1")
        // From & since are explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0")
        // From is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.*").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0")
        // Since is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0", "1.0.1")
        // None is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.*").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0", "1.0.1")
    }
}