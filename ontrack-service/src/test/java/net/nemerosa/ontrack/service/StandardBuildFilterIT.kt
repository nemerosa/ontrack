package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.security.PromotionRunCreate
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.PromotionRun.Companion.of
import net.nemerosa.ontrack.model.structure.Signature.Companion.of
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.function.Consumer

class StandardBuildFilterIT : AbstractBuildFilterIT() {

    /**
     * Default filter
     */
    @Test
    fun default_filter() {
        (1..20).forEach { build(it) }
        val builds = buildFilterService.defaultFilterProviderData().filterBranchBuilds(branch)
        Assert.assertEquals(10, builds.size.toLong())
        Assert.assertEquals("20", builds[0].name)
        Assert.assertEquals("11", builds[9].name)
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2
     * 3
     * 4 --> COPPER
     * 5 --> BRONZE
    </pre> *
     *
     *  * Since promotion level: COPPER
     *
     *
     *
     * Builds 5 and 4 must be displayed.
     */
    @Test
    @Throws(Exception::class)
    fun since_promotion_level() { // Builds
        build(1)
        build(2)
        build(3)
        build(4).withPromotion(copper)
        build(5).withPromotion(bronze)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("COPPER")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4)
    }

    /**
     * Does not return a result when the promotion in since promotion does not exist.
     */
    @Test
    fun since_unknown_promotion_level() { // Builds
        build(1)
        build(2)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("UNKNOWN")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 2, 1)
    }

    /**
     * Since a not found promotion level
     */
    @Test
    fun since_promotion_level_not_found() { // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("NOT_FOUND")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // No build being returned
        Assert.assertTrue("No build returned on promotion level not found", builds.isEmpty())
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2 --> COPPER
     * 3
     * 4 --> COPPER
     * 5 --> COPPER, BRONZE
    </pre> *
     *
     *  * With promotion level: COPPER
     *  * Since promotion level: BRONZE
     *
     *
     *
     * Build 5 should be accepted and no further build should be scan for:
     */
    @Test
    @Throws(Exception::class)
    fun with_since_promotion_level() { // Builds
        build(1)
        build(2).withPromotion(copper)
        build(3)
        build(4).withPromotion(copper)
        build(5).withPromotion(copper).withPromotion(bronze)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("BRONZE")
                .withWithPromotionLevel("COPPER")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5)
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2 --> COPPER
     * 3
     * 4 --> COPPER
     * 5 --> COPPER, BRONZE
    </pre> *
     *
     *  * With promotion level: COPPER
     *
     *
     *
     * Build 5, 4, 2 should be accepted
     */
    @Test
    @Throws(Exception::class)
    fun with_promotion_level() { // Builds
        build(1)
        build(2).withPromotion(copper)
        build(3)
        build(4).withPromotion(copper)
        build(5).withPromotion(copper).withPromotion(bronze)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithPromotionLevel("COPPER")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4, 2)
    }

    /**
     * Does not return a result when the promotion in with promotion does not exist.
     */
    @Test
    fun with_unknown_promotion_level() { // Builds
        build(1)
        build(2)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithPromotionLevel("UNKNOWN")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        Assert.assertTrue("Expecting no result", builds.isEmpty())
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2 --> PUBLICATION (success)
     * 3
     * 4 --> PUBLICATION, PRODUCTION
     * 5 --> PUBLICATION (failed)
    </pre> *
     *
     *  * With validation stamp: PUBLICATION
     *
     *
     *
     * Build 5, 4, 2 should be accepted
     */
    @Test
    @Throws(Exception::class)
    fun with_validation_stamp() { // Builds
        build(1)
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
        build(3)
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED)
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("PUBLICATION")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4, 2)
    }

    /**
     * Does not return a result when the validation stamp in with validation does not exist.
     */
    @Test
    fun with_unknown_validation_stamp() { // Builds
        build(1)
        build(2)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("UNKNOWN")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        Assert.assertTrue("Expecting no result", builds.isEmpty())
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2 --> PUBLICATION (success)
     * 3
     * 4 --> PUBLICATION, PRODUCTION
     * 5 --> PUBLICATION (failed)
    </pre> *
     *
     *  * With validation stamp: PUBLICATION
     *  * With validation stamp status: PASSED
     *
     *
     *
     * Build 4 and 2 should be accepted
     */
    @Test
    @Throws(Exception::class)
    fun with_validation_stamp_status() { // Builds
        build(1)
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
        build(3)
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED)
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("PUBLICATION")
                .withWithValidationStampStatus("PASSED")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4, 2)
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2 --> PUBLICATION (success)
     * 3
     * 4 --> PUBLICATION, PRODUCTION
     * 5 --> PUBLICATION (failed)
    </pre> *
     *
     *  * Since validation stamp: PRODUCTION
     *
     *
     *
     * Build 5 and 4 should be accepted
     */
    @Test
    @Throws(Exception::class)
    fun since_validation_stamp() { // Builds
        build(1)
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
        build(3)
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED)
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("PRODUCTION")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4)
    }

    /**
     * Does not return a result when the validation stamp in since validation does not exist.
     */
    @Test
    fun since_unknown_validation_stamp() { // Builds
        build(1)
        build(2)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("UNKNOWN")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 2, 1)
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2 --> PUBLICATION (success)
     * 3
     * 4 --> PUBLICATION, PRODUCTION
     * 5 --> PUBLICATION (failed)
    </pre> *
     *
     *  * Since validation stamp: PUBLICATION
     *  * Since validation stamp status: FAILED
     *
     *
     *
     * Build 5 should be accepted
     */
    @Test
    @Throws(Exception::class)
    fun since_validation_stamp_status() { // Builds
        build(1)
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
        build(3)
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED)
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("PUBLICATION")
                .withSinceValidationStampStatus("FAILED")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5)
    }

    /**
     * Tests the following sequence:
     *
     *
     * <pre>
     * 1
     * 2 --> PUBLICATION (success) + COPPER + BRONZE
     * 3
     * 4 --> PUBLICATION, PRODUCTION + COPPER
     * 5 --> PUBLICATION (failed)
    </pre> *
     *
     *  * Since validation stamp: PRODUCTION
     *  * Since production level: BRONZE
     *
     *
     *
     * Build 5 and 4 should be accepted
     */
    @Test
    @Throws(Exception::class)
    fun since_validation_stamp_and_since_promotion_level() { // Builds
        build(1)
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
                .withPromotion(copper)
                .withPromotion(bronze)
        build(3)
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
                .withValidation(production, ValidationRunStatusID.STATUS_PASSED)
                .withPromotion(copper)
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("PRODUCTION")
                .withSincePromotionLevel("BRONZE")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4)
    }

    @Test
    fun with_validation_stamp_not_found() {
        build(1)
        build(2)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("NOT_FOUND")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // No build
        Assert.assertTrue("Expecting no result", builds.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun after_date() { // Builds
        build(1, LocalDateTime.of(2014, 7, 14, 13, 25, 0))
        build(2, LocalDateTime.of(2014, 7, 15, 15, 0, 0))
        build(3, LocalDateTime.of(2014, 7, 16, 9, 0, 0))
        build(4, LocalDateTime.of(2014, 7, 17, 7, 0, 0))
        build(5, LocalDateTime.of(2014, 7, 18, 17, 0, 0))
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withAfterDate(LocalDate.of(2014, 7, 16))
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4, 3)
    }

    @Test
    @Throws(Exception::class)
    fun before_date() { // Builds
        build(1, LocalDateTime.of(2014, 7, 14, 13, 25, 0))
        build(2, LocalDateTime.of(2014, 7, 15, 15, 0, 0))
        build(3, LocalDateTime.of(2014, 7, 16, 9, 0, 0))
        build(4, LocalDateTime.of(2014, 7, 17, 7, 0, 0))
        build(5, LocalDateTime.of(2014, 7, 18, 17, 0, 0))
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withBeforeDate(LocalDate.of(2014, 7, 16))
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 3, 2, 1)
    }

    @Test
    @Throws(Exception::class)
    fun before_and_after_date() { // Builds
        build(1, LocalDateTime.of(2014, 7, 14, 13, 25, 0))
        build(2, LocalDateTime.of(2014, 7, 15, 15, 0, 0))
        build(3, LocalDateTime.of(2014, 7, 16, 9, 0, 0))
        build(4, LocalDateTime.of(2014, 7, 17, 7, 0, 0))
        build(5, LocalDateTime.of(2014, 7, 18, 17, 0, 0))
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withBeforeDate(LocalDate.of(2014, 7, 17))
                .withAfterDate(LocalDate.of(2014, 7, 15))
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4, 3, 2)
    }

    @Test
    @Throws(Exception::class)
    fun linked_from_project() {
        val otherBranch = doCreateBranch()
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        // Builds
        build(1)
        build(2).linkedFrom(otherBuild1)
        build(3)
        build(4).linkedFrom(otherBuild2)
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(otherBranch.project.name)
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4, 2)
    }

    @Test
    @Throws(Exception::class)
    fun linked_from_project_and_promotion() {
        val otherBranch = doCreateBranch()
        val otherPL = doCreatePromotionLevel(otherBranch, nd("PRODUCTION", ""))
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        asUser().with(otherBranch, PromotionRunCreate::class.java).execute {
            structureService.newPromotionRun(
                    of(
                            otherBuild2,
                            otherPL,
                            of("test"),
                            ""
                    )
            )
        }
        // Builds
        build(1)
        build(2).linkedFrom(otherBuild1)
        build(3)
        build(4).linkedFrom(otherBuild2)
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(otherBranch.project.name)
                .withLinkedFromPromotion("PRODUCTION")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4)
    }

    @Test
    @Throws(Exception::class)
    fun linked_from_project_and_build() {
        val otherBranch = doCreateBranch()
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        val otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""))
        // Builds
        build(1)
        build(2).linkedFrom(otherBuild1)
        build(3)
        build(4).linkedFrom(otherBuild2)
        build(5).linkedFrom(otherBuild3)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(String.format(
                        "%s:2.0",
                        otherBranch.project.name
                ))
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4)
    }

    @Test
    @Throws(Exception::class)
    fun linked_from_project_and_build_pattern() {
        val otherBranch = doCreateBranch()
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        val otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""))
        // Builds
        build(1)
        build(2).linkedFrom(otherBuild1)
        build(3)
        build(4).linkedFrom(otherBuild2)
        build(5).linkedFrom(otherBuild3)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(String.format(
                        "%s:2.*",
                        otherBranch.project.name
                ))
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4)
    }

    @Test
    @Throws(Exception::class)
    fun linked_to_project() {
        val otherBranch = doCreateBranch()
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        // Builds
        build(1)
        build(2).linkedTo(otherBuild1)
        build(3)
        build(4).linkedTo(otherBuild2)
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(otherBranch.project.name)
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4, 2)
    }

    @Test
    @Throws(Exception::class)
    fun linked_to_project_and_promotion() {
        val otherBranch = doCreateBranch()
        val otherPL = doCreatePromotionLevel(otherBranch, nd("PRODUCTION", ""))
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        asUser().with(otherBranch, PromotionRunCreate::class.java).execute {
            structureService.newPromotionRun(
                    of(
                            otherBuild2,
                            otherPL,
                            of("test"),
                            ""
                    )
            )
        }
        // Builds
        build(1)
        build(2).linkedTo(otherBuild1)
        build(3)
        build(4).linkedTo(otherBuild2)
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(otherBranch.project.name)
                .withLinkedToPromotion("PRODUCTION")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4)
    }

    @Test
    @Throws(Exception::class)
    fun linked_to_project_and_build() {
        val otherBranch = doCreateBranch()
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        val otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""))
        // Builds
        build(1)
        build(2).linkedTo(otherBuild1)
        build(3)
        build(4).linkedTo(otherBuild2)
        build(5).linkedTo(otherBuild3)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(String.format(
                        "%s:2.0",
                        otherBranch.project.name
                ))
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4)
    }

    @Test
    @Throws(Exception::class)
    fun linked_to_project_and_build_pattern() {
        val otherBranch = doCreateBranch()
        val otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""))
        val otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""))
        val otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""))
        // Builds
        build(1)
        build(2).linkedTo(otherBuild1)
        build(3)
        build(4).linkedTo(otherBuild2)
        build(5).linkedTo(otherBuild3)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(String.format(
                        "%s:2.*",
                        otherBranch.project.name
                ))
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4)
    }

    @Test
    @Throws(Exception::class)
    fun with_property() { // Builds
        build(1)
        build(2).withProperty("ananas")
        build(3)
        build(4).withProperty("an apple")
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithProperty(TestSimplePropertyType::class.java.name)
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4, 2)
    }

    @Test
    @Throws(Exception::class)
    fun with_property_value_pattern() { // Builds
        build(1)
        build(2).withProperty("ananas")
        build(3).withProperty("coconut")
        build(4).withProperty("an apple")
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithProperty(TestSimplePropertyType::class.java.name)
                .withWithPropertyValue("an")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 4, 2)
    }

    @Test
    @Throws(Exception::class)
    fun with_property_value() { // Builds
        build(1)
        build(2).withProperty("ananas")
        build(3).withProperty("coconut")
        build(4).withProperty("an apple")
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withWithProperty(TestSimplePropertyType::class.java.name)
                .withWithPropertyValue("ananas")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 2)
    }

    @Test
    @Throws(Exception::class)
    fun since_property() { // Builds
        build(1)
        build(2).withProperty("ananas")
        build(3)
        build(4).withProperty("an apple")
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSinceProperty(TestSimplePropertyType::class.java.name)
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4)
    }

    @Test
    @Throws(Exception::class)
    fun since_property_value_pattern() { // Builds
        build(1)
        build(2).withProperty("ananas")
        build(3).withProperty("coconut")
        build(4).withProperty("an apple")
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSinceProperty(TestSimplePropertyType::class.java.name)
                .withSincePropertyValue("an")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4)
    }

    @Test
    @Throws(Exception::class)
    fun since_property_value() { // Builds
        build(1)
        build(2).withProperty("ananas")
        build(3).withProperty("coconut")
        build(4).withProperty("an apple")
        build(5)
        // Filter
        val filter = buildFilterService.standardFilterProviderData(5)
                .withSinceProperty(TestSimplePropertyType::class.java.name)
                .withSincePropertyValue("ananas")
                .build()
        // Filtering
        val builds = filter.filterBranchBuilds(branch)
        // Checks the list
        checkList(builds, 5, 4, 3, 2)
    }
}