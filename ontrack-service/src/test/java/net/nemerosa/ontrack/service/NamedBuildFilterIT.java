package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.Build;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
public class NamedBuildFilterIT extends AbstractBuildFilterIT {

    @Autowired
    private BuildFilterService buildFilterService;

    protected BuildFilterProviderData<NamedBuildFilterData> filter(NamedBuildFilterData data) {
        return buildFilterService.getBuildFilterProviderData(
                NamedBuildFilterProvider.class.getName(),
                data
        );
    }

    @Test
    public void from_only_with_explicit_build() throws Exception {
        // Builds
        build("1.0.0");
        build("1.0.1");
        build("1.1.0");
        build("2.0.0");
        build("2.0.1");
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("1.1.0")).filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, "2.0.1", "2.0.0", "1.1.0");
    }

    @Test
    public void from_only_with_pattern() throws Exception {
        // Builds
        build("1.0.0");
        build("1.0.1");
        build("1.1.0");
        build("2.0.0");
        build("2.0.1");
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("1.0.*")).filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, "2.0.1", "2.0.0", "1.1.0", "1.0.1");
    }

    @Test
    public void from_only_with_explicit_build_and_promotion_before_from() throws Exception {
        // Builds
        build("1.0.0").withPromotion(bronze);
        build("1.0.1");
        build("1.1.0");
        build("2.0.0");
        build("2.0.1").withPromotion(bronze);
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("1.1.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, "2.0.1");
    }

    @Test
    public void from_only_with_explicit_build_and_promotion_after_from() throws Exception {
        // Builds
        build("1.0.0");
        build("1.0.1");
        build("1.1.0").withPromotion(bronze);
        build("2.0.0");
        build("2.0.1").withPromotion(bronze);
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("1.0.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, "2.0.1", "1.1.0");
    }

    @Test
    public void from_only_with_explicit_build_and_promotion_on_from() throws Exception {
        // Builds
        build("1.0.0");
        build("1.0.1");
        build("1.1.0").withPromotion(bronze);
        build("2.0.0");
        build("2.0.1").withPromotion(bronze);
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("1.1.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, "2.0.1", "1.1.0");
    }

    @Test
    public void from_only_with_pattern_and_promotions() throws Exception {
        // Builds
        build("1.0.0");
        build("1.0.1").withPromotion(bronze);
        build("1.1.0");
        build("2.0.0").withPromotion(bronze);
        build("2.0.1");
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("1.0.*").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch);
        // Checks the list
        checkList(builds, "2.0.0", "1.0.1");
    }

    @Test
    public void no_from() throws Exception {
        // Builds
        build("1.0.0");
        build("1.1.0");
        build("2.0.1");
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("0.9.0")).filterBranchBuilds(branch);
        // Checks the list
        assertTrue(builds.isEmpty());
    }

    @Test
    public void no_from_with_pattern() throws Exception {
        // Builds
        build("1.0.0");
        build("1.1.0");
        build("2.0.1");
        // Filter
        List<Build> builds = filter(NamedBuildFilterData.of("0.9.*")).filterBranchBuilds(branch);
        // Checks the list
        assertTrue(builds.isEmpty());
    }

    @Test
    public void from_and_since_with_no_promotion_criteria() throws Exception {
        // Builds
        build("1.0.0");
        build("1.0.1");
        build("1.1.0");
        build("2.0.0");
        build("2.0.1");
        // From & since are explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.0")).filterBranchBuilds(branch),
                "2.0.0", "1.1.0");
        // From is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.*")).filterBranchBuilds(branch),
                "2.0.1", "2.0.0", "1.1.0");
        // Since is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.0")).filterBranchBuilds(branch),
                "2.0.0", "1.1.0", "1.0.1");
        // None is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.*")).filterBranchBuilds(branch),
                "2.0.1", "2.0.0", "1.1.0", "1.0.1");
    }

    @Test
    public void from_and_since_with_promotion() throws Exception {
        // Builds
        build("1.0.0");
        build("1.0.1").withPromotion(bronze);
        build("1.1.0");
        build("2.0.0").withPromotion(bronze);
        build("2.0.1");
        // From & since are explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0");
        // From is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.1.0").withToBuild("2.0.*").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0");
        // Since is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.0").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0", "1.0.1");
        // None is explicit
        checkList(
                filter(NamedBuildFilterData.of("1.0.*").withToBuild("2.0.*").withWithPromotionLevel("BRONZE")).filterBranchBuilds(branch),
                "2.0.0", "1.0.1");
    }

}