package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildView;
import net.nemerosa.ontrack.model.structure.StructureService;

import java.util.List;
import java.util.function.Supplier;

@Data
@Deprecated
public class BuildIntervalFilter implements BuildFilter {

    private final StructureService structureService;
    private final BuildIntervalFilterData data;

    private int mostRecentId;
    private int leastRecentId;

    @Override
    public void init(Branch branch) {
        // Loads the two build boundaries
        mostRecentId = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), data.getFrom())
                .orElseThrow(() -> new BuildNotFoundException(branch.getProject().getName(), branch.getName(), data.getFrom()))
                .id();
        leastRecentId = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), data.getTo())
                .orElseThrow(() -> new BuildNotFoundException(branch.getProject().getName(), branch.getName(), data.getTo()))
                .id();
        // Reordering of the builds
        // mostRecentId > leastRecentId
        if (mostRecentId < leastRecentId) {
            int t = mostRecentId;
            mostRecentId = leastRecentId;
            leastRecentId = t;
        }
    }

    @Override
    public BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier) {
        int currentId = build.id();
        // Created after the from - not retained, going on
        if (currentId > mostRecentId) {
            return BuildFilterResult.notAccept().goingOn();
        }
        // Between from and to - retained, going on
        else if (currentId <= mostRecentId && currentId >= leastRecentId) {
            return BuildFilterResult.accept().goingOn();
        }
        // Before to - not retained, stopping
        else {
            return BuildFilterResult.stopNow();
        }
    }

}
