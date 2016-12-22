package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildView;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Data
@Deprecated
public class NamedBuildFilter implements BuildFilter {

    private final NamedBuildFilterData data;

    private Build from;
    private Build to;

    @Override
    public BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier) {

        // Compilation of regular expression
        Pattern fromPattern = Pattern.compile(data.getFromBuild());
        Pattern toPattern = StringUtils.isNotBlank(data.getToBuild()) ? Pattern.compile(data.getToBuild()) : null;

        // First from?
        if (from == null && isFromBuild(build, buildViewSupplier, fromPattern)) {
            // Keeps this build and goes on
            from = build;
            return BuildFilterResult.ok();
        }

        // First to?
        if (from != null && to == null && isToBuild(build, buildViewSupplier, fromPattern, toPattern)) {
            // Keeps this build and stops
            to = build;
            return BuildFilterResult.accept().stop();
        }

        // Neither a from or to?
        // Just goes on without accepting the build
        return BuildFilterResult.notAccept().goingOn();

    }

    private boolean isToBuild(Build build, Supplier<BuildView> buildViewSupplier, Pattern fromPattern, Pattern toPattern) {
        return !isFromBuild(build, buildViewSupplier, fromPattern)
                && (toPattern == null || toPattern.matcher(build.getName()).matches())
                && matchesPromotionLevel(buildViewSupplier);
    }

    private boolean isFromBuild(Build build, Supplier<BuildView> buildViewSupplier, Pattern fromPattern) {
        return fromPattern.matcher(build.getName()).matches()
                && matchesPromotionLevel(buildViewSupplier);
    }

    private boolean matchesPromotionLevel(Supplier<BuildView> buildViewSupplier) {
        return StringUtils.isBlank(data.getWithPromotionLevel()) ||
                buildViewSupplier.get().getPromotionRuns().stream()
                        .filter(run -> data.getWithPromotionLevel().equals(run.getPromotionLevel().getName()))
                        .findAny()
                        .isPresent();
    }

}
