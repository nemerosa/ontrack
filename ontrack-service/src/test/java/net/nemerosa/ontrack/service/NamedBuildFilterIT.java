package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.Build;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

}