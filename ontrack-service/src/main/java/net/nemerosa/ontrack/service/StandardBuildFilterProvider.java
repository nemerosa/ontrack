package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProvider;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class StandardBuildFilterProvider extends AbstractBuildFilterProvider {

    private final StructureService structureService;

    @Autowired
    public StandardBuildFilterProvider(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public String getName() {
        return "Standard filter";
    }

    @Override
    public BuildFilter filter(ID branchId, Map<String, String> parameters) {
        StandardBuildFilter filter = StandardBuildFilter.of(BuildFilterProvider.getIntParameter(parameters, "count", 10));
        filter = filter.sincePromotionLevel(BuildFilterProvider.getParameter(parameters, "sincePromotionLevel"));
        filter = filter.withPromotionLevel(BuildFilterProvider.getParameter(parameters, "withPromotionLevel"));
        // TODO sinceValidationStamps
        // TODO withValidationStamps
        // TODO withProperty
        return filter;
    }

    @Override
    protected boolean isPredefined() {
        return false;
    }

    @Override
    protected Form blankForm(ID branchId) {
        // Promotion levels for this branch
        List<PromotionLevel> promotionLevels = structureService.getPromotionLevelListForBranch(branchId);
        // Form
        return Form.create()
                .with(
                        Int.of("count")
                                .label("Maximum count")
                                .help("Maximum number of builds to display")
                                .min(1)
                                .value(10)
                )
                .with(
                        Selection.of("sincePromotionLevel")
                                .label("Since promotion level")
                                .help("Builds since the last one which was promoted to this level")
                                .items(promotionLevels)
                                .itemId("name")
                                .optional()
                )
                .with(
                        Selection.of("withPromotionLevel")
                                .label("With promotion level")
                                .help("Builds with this promotion level")
                                .items(promotionLevels)
                                .itemId("name")
                                .optional()
                )
                // TODO sinceValidationStamps
                // TODO withValidationStamps
                // TODO withProperty
                ;
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    private static class StandardBuildFilter implements BuildFilter {

        private final int count;
        private final String sincePromotionLevel;
        private final String withPromotionLevel;

        public static StandardBuildFilter of(int count) {
            return new StandardBuildFilter(count, null, null);
        }

        public StandardBuildFilter withPromotionLevel(String withPromotionLevel) {
            return new StandardBuildFilter(
                    count,
                    sincePromotionLevel,
                    withPromotionLevel
            );
        }

        public StandardBuildFilter sincePromotionLevel(String sincePromotionLevel) {
            return new StandardBuildFilter(
                    count,
                    sincePromotionLevel,
                    withPromotionLevel
            );
        }

        @Override
        public BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier) {
            // Count test
            if (builds.size() >= count) {
                return BuildFilterResult.stopNow();
            }
            // Result by default is: accept and go on
            BuildFilterResult result = BuildFilterResult.ok();
            // With promotion level
            if (isNotBlank(withPromotionLevel)) {
                result = result.acceptIf(
                        buildViewSupplier.get().getPromotionRuns().stream()
                                .filter(run -> withPromotionLevel.equals(run.getPromotionLevel().getName()))
                                .findAny()
                                .isPresent()
                );
            }
            // Since promotion level
            if (isNotBlank(sincePromotionLevel)) {
                result = result.goOnIf(
                        !buildViewSupplier.get().getPromotionRuns().stream()
                                .filter(run -> sincePromotionLevel.equals(run.getPromotionLevel().getName()))
                                .findAny()
                                .isPresent()
                );
            }
            // TODO sinceValidationStamps
            // TODO withValidationStamps
            // TODO withProperty
            // OK
            return result;
        }
    }

}
