package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static org.junit.Assert.assertEquals;

public class StandardBuildFilterIT extends AbstractServiceTestSupport {

    @Autowired
    private StructureService structureService;
    @Autowired
    private BuildFilterService buildFilterService;

    private Branch branch;
    private PromotionLevel copper;
    private PromotionLevel bronze;
    private ValidationStamp publication;
    private ValidationStamp production;

    @Before
    public void prepare() throws Exception {
        branch = doCreateBranch();
        copper = doCreatePromotionLevel(branch, nd("COPPER", ""));
        bronze = doCreatePromotionLevel(branch, nd("BRONZE", ""));
        publication = doCreateValidationStamp(branch, nd("PUBLICATION", ""));
        production = doCreateValidationStamp(branch, nd("PRODUCTION", ""));
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
     * Since a not found promotion level
     */
    @Test(expected = PromotionLevelNotFoundException.class)
    public void since_promotion_level_not_found() throws Exception {
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withSincePromotionLevel("NOT_FOUND")
                .build();
        // Filtering
        filter.filterBranchBuilds(branch);
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

    @Test(expected = ValidationStampNotFoundException.class)
    public void with_validation_stamp_not_found() throws Exception {
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("NOT_FOUND")
                .build();
        // Filtering
        filter.filterBranchBuilds(branch);
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

    // =======================================
    // Utility methods for tests
    // =======================================

    protected BuildCreator build(int name) throws Exception {
        return build(String.valueOf(name));
    }

    protected BuildCreator build(String name) throws Exception {
        return build(name, LocalDateTime.of(2014, 7, 14, 13, 25, 0));
    }

    protected BuildCreator build(int name, LocalDateTime dateTime) throws Exception {
        return build(String.valueOf(name), dateTime);
    }

    protected BuildCreator build(String name, LocalDateTime dateTime) throws Exception {
        Build build = asUser().with(branch, BuildCreate.class).call(() ->
                structureService.newBuild(
                        Build.of(
                                branch,
                                new NameDescription(name, "Build " + name),
                                Signature.of("user").withTime(dateTime)
                        )
                )
        );
        return new BuildCreator(build);
    }

    @Data
    protected class BuildCreator {

        private final Build build;

        public BuildCreator withPromotion(PromotionLevel promotionLevel) throws Exception {
            asUser().with(branch, PromotionRunCreate.class).call(() ->
                    structureService.newPromotionRun(
                            PromotionRun.of(
                                    build,
                                    promotionLevel,
                                    Signature.of("user"),
                                    ""
                            )
                    )
            );
            return this;
        }

        public BuildCreator withValidation(ValidationStamp stamp, ValidationRunStatusID status) throws Exception {
            asUser().with(branch, ValidationRunCreate.class).call(() ->
                    structureService.newValidationRun(
                            ValidationRun.of(
                                    build,
                                    stamp,
                                    1,
                                    Signature.of("user"),
                                    status,
                                    ""
                            )
                    )
            );
            return this;
        }

        public BuildCreator linkedFrom(Build otherBuild) throws Exception {
            asUser()
                    .with(branch, ProjectView.class)
                    .with(otherBuild, BuildEdit.class)
                    .call(() -> {
                        structureService.addBuildLink(
                                otherBuild,
                                build
                        );
                        return null;
                    });
            return this;
        }

        public BuildCreator linkedTo(Build otherBuild) throws Exception {
            asUser()
                    .with(branch, BuildEdit.class)
                    .with(otherBuild, ProjectView.class)
                    .call(() -> {
                        structureService.addBuildLink(
                                build,
                                otherBuild
                        );
                        return null;
                    });
            return this;
        }
    }

    protected void checkList(List<Build> builds, Integer... names) {
        List<String> expectedNames = Arrays.stream(names)
                .map(String::valueOf)
                .collect(Collectors.toList());
        List<String> actualNames = builds.stream()
                .map(Build::getName)
                .collect(Collectors.toList());
        assertEquals(expectedNames, actualNames);
    }

}