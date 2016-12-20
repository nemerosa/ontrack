package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildView;

import java.util.List;
import java.util.function.Supplier;

@Deprecated
public final class DefaultBuildFilter implements BuildFilter {

    public static final int MAX_COUNT = 10;
    public static final BuildFilter INSTANCE = new DefaultBuildFilter(MAX_COUNT);

    private final int maxCount;

    public DefaultBuildFilter(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier) {
        return BuildFilterResult.stopNowIf(builds.size() >= MAX_COUNT);
    }

}
