package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.form.Date;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StandardBuildFilterProvider extends AbstractBuildFilterProvider<StandardBuildFilterData> {

    private final StructureService structureService;
    private final ValidationRunStatusService validationRunStatusService;

    @Autowired
    public StandardBuildFilterProvider(StructureService structureService, ValidationRunStatusService validationRunStatusService) {
        this.structureService = structureService;
        this.validationRunStatusService = validationRunStatusService;
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
        // Validation stamps for this branch
        List<ValidationStamp> validationStamps = structureService.getValidationStampListForBranch(branchId);
        // List of validation run statuses
        List<ValidationRunStatusID> statuses = new ArrayList<>(validationRunStatusService.getValidationRunStatusList());
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
                .with(
                        Selection.of("sinceValidationStamp")
                                .label("Since validation stamp")
                                .help("Builds since the last one which had this validation stamp")
                                .items(validationStamps)
                                .itemId("name")
                                .optional()
                )
                .with(
                        Selection.of("sinceValidationStampStatus")
                                .label("... with status")
                                .items(statuses)
                                .optional()
                )
                .with(
                        Selection.of("withValidationStamp")
                                .label("With validation stamp")
                                .help("Builds with this validation stamp")
                                .items(validationStamps)
                                .itemId("name")
                                .optional()
                )
                .with(
                        Selection.of("withValidationStampStatus")
                                .label("... with status")
                                .items(statuses)
                                .optional()
                )
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
                .fill("beforeDate", data.getBeforeDate())
                .fill("sinceValidationStamp", data.getSinceValidationStamp())
                .fill("sinceValidationStampStatus", data.getSinceValidationStampStatus())
                .fill("withValidationStamp", data.getWithValidationStamp())
                .fill("withValidationStampStatus", data.getWithValidationStampStatus())
                // TODO withProperty
                ;
    }

    @Override
    public Optional<StandardBuildFilterData> parse(JsonNode data) {
        StandardBuildFilterData filter = StandardBuildFilterData.of(JsonUtils.getInt(data, "count", 10))
                .withSincePromotionLevel(JsonUtils.get(data, "sincePromotionLevel", null))
                .withWithPromotionLevel(JsonUtils.get(data, "withPromotionLevel", null))
                .withAfterDate(JsonUtils.getDate(data, "afterDate", null))
                .withBeforeDate(JsonUtils.getDate(data, "beforeDate", null))
                .withSinceValidationStamp(JsonUtils.get(data, "sinceValidationStamp", null))
                .withSinceValidationStampStatus(JsonUtils.get(data, "sinceValidationStampStatus", null))
                .withWithValidationStamp(JsonUtils.get(data, "withValidationStamp", null))
                .withWithValidationStampStatus(JsonUtils.get(data, "withValidationStampStatus", null))
                // TODO withProperty
                ;
        return Optional.of(filter);
    }

}
