package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.form.Date;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StandardBuildFilterProvider extends AbstractBuildFilterProvider<StandardBuildFilterData> {

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();
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
    public BuildFilter filter(ID branchId, StandardBuildFilterData data) {
        return new StandardBuildFilter(data);
    }

    @Override
    public boolean isPredefined() {
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
                .with(
                        Date.of("afterDate")
                                .label("Build after")
                                .help("Build created after or on this date")
                                .optional()
                )
                .with(
                        Date.of("beforeDate")
                                .label("Build before")
                                .help("Build created before or on this date")
                                .optional()
                )
                // TODO sinceValidationStamps
                // TODO withValidationStamps
                // TODO withProperty
                ;
    }

    @Override
    protected Form fill(Form form, StandardBuildFilterData data) {
        return form
                .fill("count", data.getCount())
                .fill("sincePromotionLevel", data.getSincePromotionLevel())
                .fill("withPromotionLevel", data.getWithPromotionLevel())
                .fill("afterDate", data.getAfterDate())
                .fill("beforeDate", data.getBeforeDate());
        // TODO sinceValidationStamps
        // TODO withValidationStamps
        // TODO withProperty
    }

    @Override
    public Optional<StandardBuildFilterData> parse(JsonNode data) {
        try {
            return Optional.of(objectMapper.treeToValue(data, StandardBuildFilterData.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

}
