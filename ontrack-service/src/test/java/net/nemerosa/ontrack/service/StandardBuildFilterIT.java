package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.PromotionRunCreate;
import net.nemerosa.ontrack.model.security.ValidationRunCreate;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Test(expected = ValidationStampNotFoundException.class)
    public void with_validation_stamp_not_found() throws Exception {
        // Filter
        BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(5)
                .withWithValidationStamp("NOT_FOUND")
                .build();
        // Filtering
        filter.filterBranchBuilds(branch);
    }

    protected BuildCreator build(int name) throws Exception {
        return build(String.valueOf(name));
    }

    protected BuildCreator build(String name) throws Exception {
        Build build = asUser().with(branch, BuildCreate.class).call(() ->
                structureService.newBuild(
                        Build.of(
                                branch,
                                new NameDescription(name, "Build " + name),
                                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 14, 13, 25, 0))
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