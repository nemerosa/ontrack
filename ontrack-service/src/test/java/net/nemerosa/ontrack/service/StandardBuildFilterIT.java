package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException;
import net.nemerosa.ontrack.model.security.PromotionRunCreate;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static net.nemerosa.ontrack.test.TestUtils.range;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StandardBuildFilterIT extends AbstractBuildFilterIT {

    /**
     * Default filter
     */
    @Test
    public void default_filter() {
        range(1, 20).forEach(this::build);
        List<Build> builds = buildFilterService.defaultFilterProviderData().filterBranchBuilds(branch);
        assertEquals(10, builds.size());
        assertEquals("20", builds.get(0).getName());
        assertEquals("11", builds.get(9).getName());
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2
     *     3
     *     4 --> COPPER
     *     5 --> BRONZE
     * </pre>
     * <ul>
     * <li>Since promotion level: COPPER</li>
     * </ul>
     * <p>
     * Builds 5 and 4 must be displayed.
     */
    @Test
    public void since_promotion_level() throws Exception {
        // Builds
        build(1);
        build(2);
        build(3);
        build(4).withPromotion(copper);
        build(5).withPromotion(bronze);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("COPPER")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4);
    }

    /**
     * Does not return a result when the promotion in since promotion does not exist.
     */
    @Test
    public void since_unknown_promotion_level() {
        // Builds
        build(1);
        build(2);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("UNKNOWN")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 2, 1);
    }

    /**
     * Since a not found promotion level
     */
    @Test
    public void since_promotion_level_not_found() {
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("NOT_FOUND")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // No build being returned
        assertTrue("No build returned on promotion level not found", builds.isEmpty());
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> COPPER
     *     3
     *     4 --> COPPER
     *     5 --> COPPER, BRONZE
     * </pre>
     * <ul>
     * <li>With promotion level: COPPER</li>
     * <li>Since promotion level: BRONZE</li>
     * </ul>
     * <p>
     * Build 5 should be accepted and no further build should be scan for:
     */
    @Test
    public void with_since_promotion_level() throws Exception {
        // Builds
        build(1);
        build(2).withPromotion(copper);
        build(3);
        build(4).withPromotion(copper);
        build(5).withPromotion(copper).withPromotion(bronze);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("BRONZE")
                .withWithPromotionLevel("COPPER")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5);
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> COPPER
     *     3
     *     4 --> COPPER
     *     5 --> COPPER, BRONZE
     * </pre>
     * <ul>
     * <li>With promotion level: COPPER</li>
     * </ul>
     * <p>
     * Build 5, 4, 2 should be accepted
     */
    @Test
    public void with_promotion_level() throws Exception {
        // Builds
        build(1);
        build(2).withPromotion(copper);
        build(3);
        build(4).withPromotion(copper);
        build(5).withPromotion(copper).withPromotion(bronze);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithPromotionLevel("COPPER")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4, 2);
    }

    /**
     * Does not return a result when the promotion in with promotion does not exist.
     */
    @Test
    public void with_unknown_promotion_level() {
        // Builds
        build(1);
        build(2);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithPromotionLevel("UNKNOWN")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        assertTrue("Expecting no result", builds.isEmpty());
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> PUBLICATION (success)
     *     3
     *     4 --> PUBLICATION, PRODUCTION
     *     5 --> PUBLICATION (failed)
     * </pre>
     * <ul>
     * <li>With validation stamp: PUBLICATION</li>
     * </ul>
     * <p>
     * Build 5, 4, 2 should be accepted
     */
    @Test
    public void with_validation_stamp() throws Exception {
        // Builds
        build(1);
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED);
        build(3);
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED);
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("PUBLICATION")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4, 2);
    }

    /**
     * Does not return a result when the validation stamp in with validation does not exist.
     */
    @Test
    public void with_unknown_validation_stamp() {
        // Builds
        build(1);
        build(2);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("UNKNOWN")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        assertTrue("Expecting no result", builds.isEmpty());
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> PUBLICATION (success)
     *     3
     *     4 --> PUBLICATION, PRODUCTION
     *     5 --> PUBLICATION (failed)
     * </pre>
     * <ul>
     * <li>With validation stamp: PUBLICATION</li>
     * <li>With validation stamp status: PASSED</li>
     * </ul>
     * <p>
     * Build 4 and 2 should be accepted
     */
    @Test
    public void with_validation_stamp_status() throws Exception {
        // Builds
        build(1);
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED);
        build(3);
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED);
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("PUBLICATION")
                .withWithValidationStampStatus("PASSED")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4, 2);
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> PUBLICATION (success)
     *     3
     *     4 --> PUBLICATION, PRODUCTION
     *     5 --> PUBLICATION (failed)
     * </pre>
     * <ul>
     * <li>Since validation stamp: PRODUCTION</li>
     * </ul>
     * <p>
     * Build 5 and 4 should be accepted
     */
    @Test
    public void since_validation_stamp() throws Exception {
        // Builds
        build(1);
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED);
        build(3);
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED);
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("PRODUCTION")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4);
    }

    /**
     * Does not return a result when the validation stamp in since validation does not exist.
     */
    @Test
    public void since_unknown_validation_stamp() {
        // Builds
        build(1);
        build(2);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("UNKNOWN")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 2, 1);
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> PUBLICATION (success)
     *     3
     *     4 --> PUBLICATION, PRODUCTION
     *     5 --> PUBLICATION (failed)
     * </pre>
     * <ul>
     * <li>Since validation stamp: PUBLICATION</li>
     * <li>Since validation stamp status: FAILED</li>
     * </ul>
     * <p>
     * Build 5 should be accepted
     */
    @Test
    public void since_validation_stamp_status() throws Exception {
        // Builds
        build(1);
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED);
        build(3);
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED).withValidation(production, ValidationRunStatusID.STATUS_PASSED);
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("PUBLICATION")
                .withSinceValidationStampStatus("FAILED")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5);
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> PUBLICATION (success) + COPPER + BRONZE
     *     3
     *     4 --> PUBLICATION, PRODUCTION + COPPER
     *     5 --> PUBLICATION (failed)
     * </pre>
     * <ul>
     * <li>Since validation stamp: PRODUCTION</li>
     * <li>Since production level: BRONZE</li>
     * </ul>
     * <p>
     * Build 5 and 4 should be accepted
     */
    @Test
    public void since_validation_stamp_and_since_promotion_level() throws Exception {
        // Builds
        build(1);
        build(2).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
                .withPromotion(copper)
                .withPromotion(bronze);
        build(3);
        build(4).withValidation(publication, ValidationRunStatusID.STATUS_PASSED)
                .withValidation(production, ValidationRunStatusID.STATUS_PASSED)
                .withPromotion(copper);
        build(5).withValidation(publication, ValidationRunStatusID.STATUS_FAILED);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSinceValidationStamp("PRODUCTION")
                .withSincePromotionLevel("BRONZE")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4);
    }

    @Test
    public void with_validation_stamp_not_found() {
        build(1);
        build(2);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("NOT_FOUND")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // No build
        assertTrue("Expecting no result", builds.isEmpty());
    }

    @Test
    public void after_date() throws Exception {
        // Builds
        build(1, LocalDateTime.of(2014, 7, 14, 13, 25, 0));
        build(2, LocalDateTime.of(2014, 7, 15, 15, 0, 0));
        build(3, LocalDateTime.of(2014, 7, 16, 9, 0, 0));
        build(4, LocalDateTime.of(2014, 7, 17, 7, 0, 0));
        build(5, LocalDateTime.of(2014, 7, 18, 17, 0, 0));
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withAfterDate(LocalDate.of(2014, 7, 16))
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4, 3);
    }

    @Test
    public void before_date() throws Exception {
        // Builds
        build(1, LocalDateTime.of(2014, 7, 14, 13, 25, 0));
        build(2, LocalDateTime.of(2014, 7, 15, 15, 0, 0));
        build(3, LocalDateTime.of(2014, 7, 16, 9, 0, 0));
        build(4, LocalDateTime.of(2014, 7, 17, 7, 0, 0));
        build(5, LocalDateTime.of(2014, 7, 18, 17, 0, 0));
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withBeforeDate(LocalDate.of(2014, 7, 16))
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 3, 2, 1);
    }

    @Test
    public void before_and_after_date() throws Exception {
        // Builds
        build(1, LocalDateTime.of(2014, 7, 14, 13, 25, 0));
        build(2, LocalDateTime.of(2014, 7, 15, 15, 0, 0));
        build(3, LocalDateTime.of(2014, 7, 16, 9, 0, 0));
        build(4, LocalDateTime.of(2014, 7, 17, 7, 0, 0));
        build(5, LocalDateTime.of(2014, 7, 18, 17, 0, 0));
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withBeforeDate(LocalDate.of(2014, 7, 17))
                .withAfterDate(LocalDate.of(2014, 7, 15))
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4, 3, 2);
    }

    @Test
    public void linked_from_project() throws Exception {
        Branch otherBranch = doCreateBranch();
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        // Builds
        build(1);
        build(2).linkedFrom(otherBuild1);
        build(3);
        build(4).linkedFrom(otherBuild2);
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(otherBranch.getProject().getName())
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4, 2);
    }

    @Test
    public void linked_from_project_and_promotion() throws Exception {
        Branch otherBranch = doCreateBranch();
        PromotionLevel otherPL = doCreatePromotionLevel(otherBranch, nd("PRODUCTION", ""));
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        asUser().with(otherBranch, PromotionRunCreate.class).execute(() -> structureService.newPromotionRun(
                PromotionRun.of(
                        otherBuild2,
                        otherPL,
                        Signature.of("test"),
                        ""
                )
        ));
        // Builds
        build(1);
        build(2).linkedFrom(otherBuild1);
        build(3);
        build(4).linkedFrom(otherBuild2);
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(otherBranch.getProject().getName())
                .withLinkedFromPromotion("PRODUCTION")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4);
    }

    @Test
    public void linked_from_project_and_build() throws Exception {
        Branch otherBranch = doCreateBranch();
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        Build otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""));
        // Builds
        build(1);
        build(2).linkedFrom(otherBuild1);
        build(3);
        build(4).linkedFrom(otherBuild2);
        build(5).linkedFrom(otherBuild3);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(
                        String.format(
                                "%s:2.0",
                                otherBranch.getProject().getName()
                        )
                )
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4);
    }

    @Test
    public void linked_from_project_and_build_pattern() throws Exception {
        Branch otherBranch = doCreateBranch();
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        Build otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""));
        // Builds
        build(1);
        build(2).linkedFrom(otherBuild1);
        build(3);
        build(4).linkedFrom(otherBuild2);
        build(5).linkedFrom(otherBuild3);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedFrom(
                        String.format(
                                "%s:2.*",
                                otherBranch.getProject().getName()
                        )
                )
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4);
    }

    @Test
    public void linked_to_project() throws Exception {
        Branch otherBranch = doCreateBranch();
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        // Builds
        build(1);
        build(2).linkedTo(otherBuild1);
        build(3);
        build(4).linkedTo(otherBuild2);
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(otherBranch.getProject().getName())
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4, 2);
    }

    @Test
    public void linked_to_project_and_promotion() throws Exception {
        Branch otherBranch = doCreateBranch();
        PromotionLevel otherPL = doCreatePromotionLevel(otherBranch, nd("PRODUCTION", ""));
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        asUser().with(otherBranch, PromotionRunCreate.class).execute(() -> structureService.newPromotionRun(
                PromotionRun.of(
                        otherBuild2,
                        otherPL,
                        Signature.of("test"),
                        ""
                )
        ));
        // Builds
        build(1);
        build(2).linkedTo(otherBuild1);
        build(3);
        build(4).linkedTo(otherBuild2);
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(otherBranch.getProject().getName())
                .withLinkedToPromotion("PRODUCTION")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4);
    }

    @Test
    public void linked_to_project_and_build() throws Exception {
        Branch otherBranch = doCreateBranch();
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        Build otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""));
        // Builds
        build(1);
        build(2).linkedTo(otherBuild1);
        build(3);
        build(4).linkedTo(otherBuild2);
        build(5).linkedTo(otherBuild3);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(
                        String.format(
                                "%s:2.0",
                                otherBranch.getProject().getName()
                        )
                )
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4);
    }

    @Test
    public void linked_to_project_and_build_pattern() throws Exception {
        Branch otherBranch = doCreateBranch();
        Build otherBuild1 = doCreateBuild(otherBranch, nd("1.0", ""));
        Build otherBuild2 = doCreateBuild(otherBranch, nd("2.0", ""));
        Build otherBuild3 = doCreateBuild(otherBranch, nd("2.1", ""));
        // Builds
        build(1);
        build(2).linkedTo(otherBuild1);
        build(3);
        build(4).linkedTo(otherBuild2);
        build(5).linkedTo(otherBuild3);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withLinkedTo(
                        String.format(
                                "%s:2.*",
                                otherBranch.getProject().getName()
                        )
                )
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4);
    }

    @Test
    public void with_property() throws Exception {
        // Builds
        build(1);
        build(2).withProperty("ananas");
        build(3);
        build(4).withProperty("an apple");
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithProperty(TestSimplePropertyType.class.getName())
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4, 2);
    }

    @Test
    public void with_property_value_pattern() throws Exception {
        // Builds
        build(1);
        build(2).withProperty("ananas");
        build(3).withProperty("coconut");
        build(4).withProperty("an apple");
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithProperty(TestSimplePropertyType.class.getName())
                .withWithPropertyValue("an.*")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 4, 2);
    }

    @Test
    public void with_property_value() throws Exception {
        // Builds
        build(1);
        build(2).withProperty("ananas");
        build(3).withProperty("coconut");
        build(4).withProperty("an apple");
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithProperty(TestSimplePropertyType.class.getName())
                .withWithPropertyValue("ananas")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 2);
    }

    @Test
    public void since_property() throws Exception {
        // Builds
        build(1);
        build(2).withProperty("ananas");
        build(3);
        build(4).withProperty("an apple");
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSinceProperty(TestSimplePropertyType.class.getName())
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4);
    }

    @Test
    public void since_property_value_pattern() throws Exception {
        // Builds
        build(1);
        build(2).withProperty("ananas");
        build(3).withProperty("coconut");
        build(4).withProperty("an apple");
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSinceProperty(TestSimplePropertyType.class.getName())
                .withSincePropertyValue("an.*")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4);
    }

    @Test
    public void since_property_value() throws Exception {
        // Builds
        build(1);
        build(2).withProperty("ananas");
        build(3).withProperty("coconut");
        build(4).withProperty("an apple");
        build(5);
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSinceProperty(TestSimplePropertyType.class.getName())
                .withSincePropertyValue("ananas")
                .build();
        // Filtering
        List<Build> builds = filter.filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, 5, 4, 3, 2);
    }

}