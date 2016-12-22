package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException;
import net.nemerosa.ontrack.model.structure.Build;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class IntervalBuildFilterIT extends AbstractBuildFilterIT {

    @Autowired
    private BuildFilterService buildFilterService;

    protected BuildFilterProviderData<BuildIntervalFilterData> filter(BuildIntervalFilterData data) {
        return buildFilterService.getBuildFilterProviderData(
                BuildIntervalFilterProvider.class.getName(),
                data
        );
    }

    @Test(expected = BuildNotFoundException.class)
    public void from_does_not_exist() {
        filter(BuildIntervalFilterData.of("xxx", null)).filterBranchBuilds(branch);
    }

    @Test(expected = BuildNotFoundException.class)
    public void to_does_not_exist() throws Exception {
        build("1.0.0");
        filter(BuildIntervalFilterData.of("1.0.0", "xxx")).filterBranchBuilds(branch);
    }

    @Test
    public void from_only() throws Exception {
        build("1.0.0");
        build("1.0.1");
        build("1.1.0");
        List<Build> builds = filter(BuildIntervalFilterData.of("1.0.1", null)).filterBranchBuilds(branch);
        checkList(builds, "1.1.0", "1.0.1");
    }

    @Test
    public void from_and_to_only() throws Exception {
        build("1.0.0");
        build("1.0.1");
        build("1.0.2");
        build("1.1.0");
        List<Build> builds = filter(BuildIntervalFilterData.of("1.0.0", "1.0.2")).filterBranchBuilds(branch);
        checkList(builds, "1.0.2", "1.0.1", "1.0.0");
    }

}