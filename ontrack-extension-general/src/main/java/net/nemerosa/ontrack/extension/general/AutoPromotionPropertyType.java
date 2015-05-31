package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiSelection;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutoPromotionPropertyType extends AbstractPropertyType<AutoPromotionProperty> {

    private final StructureService structureService;

    public AutoPromotionPropertyType(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public String getName() {
        return "Auto promotion";
    }

    @Override
    public String getDescription() {
        return "Allows a promotion level to be granted on a build as soon as a list of validation stamps has been passed";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.PROMOTION_LEVEL);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, AutoPromotionProperty value) {
        PromotionLevel promotionLevel = (PromotionLevel) entity;
        return Form.create()
                .with(
                        MultiSelection.of("validationStamps")
                                .label("Validation stamps")
                                .items(
                                        structureService.getValidationStampListForBranch(promotionLevel.getBranch().getId())
                                                .stream()
                                                .map(vs -> new ValidationStampSelection(
                                                                vs,
                                                                value != null && value.getValidationStamps().contains(vs.getName())
                                                        )
                                                )
                                                .collect(Collectors.toList())
                                )
                                .help("When all the selected validation stamps have passed for a build, the promotion will automatically be granted.")
                );
    }

    @Override
    public AutoPromotionProperty fromClient(JsonNode node) {
        JsonNode validationStamps = node.get("validationStamps");
        if (validationStamps.isArray()) {
            List<Integer> ids = new ArrayList<>();
            validationStamps.forEach(id -> ids.add(id.asInt()));
            // Reading the validation stamps and then the names
            return new AutoPromotionProperty(
                    ids.stream()
                            .map(id -> structureService.getValidationStamp(ID.of(id)))
                            .map(ValidationStamp::getName)
                            .collect(Collectors.toList())
            );
        } else {
            throw new AutoPromotionPropertyCannotParseException("Cannot get the list of validation stamps");
        }
    }

    @Override
    public AutoPromotionProperty fromStorage(JsonNode node) {
        return parse(node, AutoPromotionProperty.class);
    }

    @Override
    public String getSearchKey(AutoPromotionProperty value) {
        return "";
    }

    @Override
    public AutoPromotionProperty replaceValue(AutoPromotionProperty value, Function<String, String> replacementFunction) {
        return value;
    }
}
