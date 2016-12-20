package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.StandardBuildFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class StandardBuildFilterProvider extends AbstractBuildFilterProvider<StandardBuildFilterData> {

    private final StructureService structureService;
    private final ValidationRunStatusService validationRunStatusService;
    private final PropertyService propertyService;
    private final StandardBuildFilterRepository standardBuildFilterRepository;

    @Autowired
    public StandardBuildFilterProvider(
            StructureService structureService,
            ValidationRunStatusService validationRunStatusService,
            PropertyService propertyService,
            StandardBuildFilterRepository standardBuildFilterRepository
    ) {
        this.structureService = structureService;
        this.validationRunStatusService = validationRunStatusService;
        this.propertyService = propertyService;
        this.standardBuildFilterRepository = standardBuildFilterRepository;
    }

    @Override
    public String getType() {
        return StandardBuildFilterProvider.class.getName();
    }

    @Override
    public String getName() {
        return "Standard filter";
    }

    @Override
    public List<Build> filterBranchBuilds(ID branchId, StandardBuildFilterData data) {
        return standardBuildFilterRepository.getBuilds(
                branchId,
                data
        );
    }

    @Override
    public BuildFilter filter(ID branchId, StandardBuildFilterData data) {
        return new StandardBuildFilter(data, propertyService, structureService);
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
        // List of properties for a build
        //noinspection Convert2MethodRef
        List<PropertyTypeDescriptor> properties = propertyService.getPropertyTypes().stream()
                .filter(type -> type.getSupportedEntityTypes().contains(ProjectEntityType.BUILD))
                .map(type -> PropertyTypeDescriptor.of(type))
                .collect(Collectors.toList());
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
                .with(
                        Selection.of("sinceProperty")
                                .label("Since property")
                                .items(properties)
                                .itemId("typeName")
                                .optional()
                )
                .with(
                        Text.of("sincePropertyValue")
                                .label("... with value")
                                .length(40)
                                .optional()
                )
                .with(
                        Selection.of("withProperty")
                                .label("With property")
                                .items(properties)
                                .itemId("typeName")
                                .optional()
                )
                .with(
                        Text.of("withPropertyValue")
                                .label("... with value")
                                .length(40)
                                .optional()
                )
                .with(
                        Text.of("linkedFrom")
                                .label("Linked from")
                                .help("The build must be linked FROM the builds selected by the pattern.\n" +
                                        "Syntax: PRJ:BLD where PRJ is a project name and BLD a build expression - with * as placeholder"
                                )
                                .length(40)
                                .optional()
                )
                .with(
                        Text.of("linkedTo")
                                .label("Linked to")
                                .help("The build must be linked TO the builds selected by the pattern.\n" +
                                        "Syntax: PRJ:BLD where PRJ is a project name and BLD a build expression - with * as placeholder"
                                )
                                .length(40)
                                .optional()
                )
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
                .fill("sinceProperty", data.getSinceProperty())
                .fill("sincePropertyValue", data.getSincePropertyValue())
                .fill("withProperty", data.getWithProperty())
                .fill("withPropertyValue", data.getWithPropertyValue())
                .fill("linkedFrom", data.getLinkedFrom())
                .fill("linkedTo", data.getLinkedTo())
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
                .withSinceProperty(JsonUtils.get(data, "sinceProperty", null))
                .withSincePropertyValue(JsonUtils.get(data, "sincePropertyValue", null))
                .withWithProperty(JsonUtils.get(data, "withProperty", null))
                .withWithPropertyValue(JsonUtils.get(data, "withPropertyValue", null))
                .withLinkedFrom(JsonUtils.get(data, "linkedFrom", null))
                .withLinkedTo(JsonUtils.get(data, "linkedTo", null));
        return Optional.of(filter);
    }

}
