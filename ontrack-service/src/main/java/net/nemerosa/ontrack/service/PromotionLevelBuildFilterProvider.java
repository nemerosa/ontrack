package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Gets each last build for each promotion level
 */
@Component
public class PromotionLevelBuildFilterProvider extends AbstractPredefinedBuildFilterProvider {

    private final StructureService structureService;

    @Autowired
    public PromotionLevelBuildFilterProvider(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public String getName() {
        return "Last per promotion level";
    }

    @Override
    public BuildFilter filter(ID branchId, Object parameters) {
        return new PromotionLevelBuildFilter(
                structureService.getPromotionLevelListForBranch(branchId)
        );
    }

    @Deprecated
    private static class PromotionLevelBuildFilter implements BuildFilter {

        private final Set<String> promotionLevelsToFill;

        public PromotionLevelBuildFilter(List<PromotionLevel> promotionLevels) {
            this.promotionLevelsToFill = new HashSet<>(
                    promotionLevels.stream().map(PromotionLevel::getName).collect(Collectors.toList())
            );
        }

        @Override
        public BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier) {
            // Gets the list of promotion runs for the build to check
            BuildView buildView = buildViewSupplier.get();
            // List of promotion levels reached by this build
            Collection<String> buildPromotionNames = buildView.getPromotionRuns().stream()
                    .map(run -> run.getPromotionLevel().getName())
                    .collect(Collectors.toList());
            // Getting the intersection
            Set<String> remainingPromotionLevelsReached = new HashSet<>(buildPromotionNames);
            remainingPromotionLevelsReached.retainAll(promotionLevelsToFill);
            // New list of promotion to fill
            promotionLevelsToFill.removeAll(remainingPromotionLevelsReached);
            // Result
            return BuildFilterResult.ok()
                    // Accept the build if at least one remaining promotion level has been reached now
                    .acceptIf(!remainingPromotionLevelsReached.isEmpty())
                            // Going on if there are still promotion levels to fill
                    .goOnIf(!promotionLevelsToFill.isEmpty());
        }
    }

}
