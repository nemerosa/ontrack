package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildView;

import java.util.List;
import java.util.function.Supplier;

public final class DefaultBuildFilter implements BuildFilter {

    public static final int MAX_COUNT = 10;
    public static final BuildFilter INSTANCE = new DefaultBuildFilter();

    private DefaultBuildFilter() {
    }

    @Override
    public BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier) {
        return BuildFilterResult.stopNowIf(builds.size() >= MAX_COUNT);
    }

}
