package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Gets each last build for each promotion level
 */
@Component
@Transactional
public class PromotionLevelBuildFilterProvider extends AbstractPredefinedBuildFilterProvider {

    private final StructureService structureService;
    private final StructureRepository structureRepository;

    @Autowired
    public PromotionLevelBuildFilterProvider(StructureService structureService, StructureRepository structureRepository) {
        this.structureService = structureService;
        this.structureRepository = structureRepository;
    }

    @Override
    public String getType() {
        return PromotionLevelBuildFilterProvider.class.getName();
    }

    @Override
    public String getName() {
        return "Last per promotion level";
    }

    @Override
    public List<Build> filterBranchBuilds(Branch branch, Object data) {
        // Gets the list of promotion levels for this branch
        List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branch.getId());
        // Index of promotion levels
        Set<Integer> index = new HashSet<>(
                promotionLevels.stream().map(Entity::id).collect(Collectors.toList())
        );
        // List of results
        List<Build> builds = new ArrayList<>();
        // Index of added builds
        Set<Integer> addedBuilds = new HashSet<>();
        // Looping over the builds until all promotion levels are filled in
        Predicate<Build> loop = build -> {
            // Gets the promotions for this build
            List<PromotionRun> promotionRuns = structureRepository.getPromotionRunsForBuild(build);
            // For each promotion...
            promotionRuns.forEach(run -> {
                int promotionLevelId = run.getPromotionLevel().id();
                if (index.contains(promotionLevelId)) {
                    index.remove(promotionLevelId);
                    if (!addedBuilds.contains(build.id())) {
                        builds.add(build);
                        addedBuilds.add(build.id());
                    }
                }
            });
            // Going on if index is not empty
            return !index.isEmpty();
        };
        // Iterates over builds
        structureRepository.builds(branch, loop);
        // OK
        return builds;
    }

}
