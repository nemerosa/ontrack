package net.nemerosa.ontrack.service

import org.junit.Test

class PromotionLevelBuildFilterIT : AbstractBuildFilterIT() {

    /**
     * Tests the following sequence:
     *
     *
     * ```
     * 1
     * 2
     * 3
     * 4 --> COPPER
     * 5 --> BRONZE
     * ```
     */
    @Test
    @Throws(Exception::class)
    fun distinct_sorted_promotion_levels() { // Builds
        build(1)
        build(2)
        build(3)
        build(4).withPromotion(copper)
        build(5).withPromotion(bronze)
        // Filter
        val builds = asUserWithView(branch).call { buildFilterService.lastPromotedBuildsFilterData().filterBranchBuilds(branch) }
        // Checks the list
        checkList(builds, 5, 4)
    }

    /**
     * Tests the following sequence:
     *
     *
     * ```
     * 1
     * 2
     * 3 --> COPPER
     * 4
     * 5 --> BRONZE
     * ```
     */
    @Test
    @Throws(Exception::class)
    fun distinct_separated_promotion_levels() { // Builds
        build(1)
        build(2)
        build(3).withPromotion(copper)
        build(4)
        build(5).withPromotion(bronze)
        // Filter
        val builds = asUserWithView(branch).call { buildFilterService.lastPromotedBuildsFilterData().filterBranchBuilds(branch) }
        // Checks the list
        checkList(builds, 5, 3)
    }

    /**
     * Tests the following sequence:
     *
     *
     * ```
     * 1
     * 2
     * 3
     * 4 --> BRONZE
     * 5 --> COPPER
     * ```
     */
    @Test
    @Throws(Exception::class)
    fun distinct_inverted_promotion_levels() { // Builds
        build(1)
        build(2)
        build(3)
        build(4).withPromotion(bronze)
        build(5).withPromotion(copper)
        // Filter
        val builds = asUserWithView(branch).call { buildFilterService.lastPromotedBuildsFilterData().filterBranchBuilds(branch) }
        // Checks the list
        checkList(builds, 5, 4)
    }

    /**
     * Tests the following sequence:
     *
     *
     * ```
     * 1
     * 2
     * 3
     * 4 --> BRONZE
     * 5 --> COPPER, BRONZE
     * ```
     */
    @Test
    @Throws(Exception::class)
    fun grouped_promotion_levels() { // Builds
        build(1)
        build(2)
        build(3)
        build(4).withPromotion(bronze)
        build(5).withPromotion(copper).withPromotion(bronze)
        // Filter
        val builds = asUserWithView(branch).call { buildFilterService.lastPromotedBuildsFilterData().filterBranchBuilds(branch) }
        // Checks the list
        checkList(builds, 5)
    }

    /**
     * Tests the following sequence:
     *
     *
     * ```
     * 1
     * 2
     * 3
     * 4 --> COPPER, BRONZE
     * 5 --> BRONZE
     * ```
     */
    @Test
    @Throws(Exception::class)
    fun grouped_promotion_levels_and_one_after() { // Builds
        build(1)
        build(2)
        build(3)
        build(4).withPromotion(copper).withPromotion(bronze)
        build(5).withPromotion(bronze)
        // Filter
        val builds = asUserWithView(branch).call { buildFilterService.lastPromotedBuildsFilterData().filterBranchBuilds(branch) }
        // Checks the list
        checkList(builds, 5, 4)
    }

    /**
     * Tests the following sequence:
     *
     *
     * ```
     * 1
     * 2
     * 3
     * 4 --> COPPER, BRONZE
     * 5 --> BRONZE
     * ```
     */
    @Test
    @Throws(Exception::class)
    fun grouped_promotion_levels_and_one_other_after() { // Builds
        build(1)
        build(2)
        build(3)
        build(4).withPromotion(copper).withPromotion(bronze)
        build(5).withPromotion(gold)
        // Filter
        val builds = asUserWithView(branch).call { buildFilterService.lastPromotedBuildsFilterData().filterBranchBuilds(branch) }
        // Checks the list
        checkList(builds, 5, 4)
    }
}