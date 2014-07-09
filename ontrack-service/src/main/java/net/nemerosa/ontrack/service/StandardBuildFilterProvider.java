package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProvider;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    public BuildFilter filter(ID branchId, Map<String, String[]> parameters) {
        StandardBuildFilter filter = StandardBuildFilter.of(BuildFilterProvider.getIntParameter(parameters, "count", 10));
        // TODO sinceValidationStamps
        // TODO withValidationStamps
        // TODO withProperty
        return filter;
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

        public static StandardBuildFilter of(int count) {
            return new StandardBuildFilter(count);
        }

        @Override
        public boolean acceptCount(int size) {
            return size <= count;
        }
    }

}
