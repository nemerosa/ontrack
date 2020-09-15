package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiSelection;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AutoPromotionPropertyType extends AbstractPropertyType<AutoPromotionProperty> {

    private final StructureService structureService;

    @Autowired
    public AutoPromotionPropertyType(GeneralExtensionFeature extensionFeature, StructureService structureService) {
        super(extensionFeature);
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
                                                                value != null && value.containsDirectValidationStamp(vs)
                                                        )
                                                )
                                                .collect(Collectors.toList())
                                )
                                .help("When all the selected validation stamps have passed for a build, the promotion will automatically be granted.")
                )
                .with(
                        Text.of("include")
                                .label("Include")
                                .optional()
                                .value(value != null ? value.getInclude() : "")
                                .help("Regular expression to select validation stamps by name")
                )
                .with(
                        Text.of("exclude")
                                .label("Exclude")
                                .optional()
                                .value(value != null ? value.getExclude() : "")
                                .help("Regular expression to exclude validation stamps by name")
                )
                .with(
                        MultiSelection.of("promotionLevels")
                                .label("Promotion levels")
                                .items(
                                        structureService.getPromotionLevelListForBranch(promotionLevel.getBranch().getId())
                                                .stream()
                                                .filter(pl -> pl.id() != promotionLevel.id())
                                                .map(pl -> new PromotionLevelSelection(
                                                                pl,
                                                                value != null && value.contains(pl)
                                                        )
                                                )
                                                .collect(Collectors.toList())
                                )
                                .help("When all the selected promotion levels have been granted to a build, the promotion will automatically be granted.")
                )
                ;
    }

    @Override
    public AutoPromotionProperty fromClient(JsonNode node) {
        return loadAutoPromotionProperty(node);
    }

    private AutoPromotionProperty loadAutoPromotionProperty(JsonNode node) {
        // Backward compatibility (before 2.14)
        if (node.isArray()) {
            return new AutoPromotionProperty(
                    readValidationStamps(node),
                    "",
                    "",
                    Collections.emptyList()
            );
        } else {
            JsonNode validationStamps = node.get("validationStamps");
            List<ValidationStamp> validationStampList = readValidationStamps(validationStamps);
            JsonNode promotionLevels = node.get("promotionLevels");
            List<PromotionLevel> promotionLevelList = readPromotionLevels(promotionLevels);
            return new AutoPromotionProperty(
                    validationStampList,
                    JsonUtils.get(node, "include", false, ""),
                    JsonUtils.get(node, "exclude", false, ""),
                    promotionLevelList
            );
        }
    }

    private List<ValidationStamp> readValidationStamps(JsonNode validationStampIds) {
        List<ValidationStamp> validationStampList;
        if (validationStampIds.isArray()) {
            List<Integer> ids = new ArrayList<>();
            validationStampIds.forEach(id -> ids.add(id.asInt()));
            // Reading the validation stamps and then the names
            validationStampList = ids.stream()
                    .map(id -> structureService.getValidationStamp(ID.of(id)))
                    .collect(Collectors.toList());
        } else {
            throw new AutoPromotionPropertyCannotParseException("Cannot get the list of validation stamps");
        }
        return validationStampList;
    }

    private List<PromotionLevel> readPromotionLevels(JsonNode promotionLevelIds) {
        if (promotionLevelIds != null) {
            if (promotionLevelIds.isArray()) {
                List<Integer> ids = new ArrayList<>();
                promotionLevelIds.forEach(id -> ids.add(id.asInt()));
                // Reading the validation stamps and then the names
                return ids.stream()
                        .map(id -> structureService.getPromotionLevel(ID.of(id)))
                        .collect(Collectors.toList());
            } else {
                throw new AutoPromotionPropertyCannotParseException("Cannot get the list of promotion levels");
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public AutoPromotionProperty copy(ProjectEntity sourceEntity, AutoPromotionProperty value, ProjectEntity targetEntity, Function<String, String> replacementFn) {
        PromotionLevel targetPromotionLevel = (PromotionLevel) targetEntity;
        return new AutoPromotionProperty(
                value.getValidationStamps().stream()
                        .map(vs -> structureService.findValidationStampByName(
                                targetPromotionLevel.getBranch().getProject().getName(),
                                targetPromotionLevel.getBranch().getName(),
                                vs.getName()
                        ))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()),
                value.getInclude(),
                value.getExclude(),
                value.getPromotionLevels().stream()
                        .map(pl -> structureService.findPromotionLevelByName(
                                targetPromotionLevel.getBranch().getProject().getName(),
                                targetPromotionLevel.getBranch().getName(),
                                pl.getName()
                        ))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
    }

    /**
     * As a list of validation stamp IDs
     */
    @Override
    public JsonNode forStorage(AutoPromotionProperty value) {
        return format(
                MapBuilder.create()
                        .with("validationStamps", value.getValidationStamps().stream()
                                .map(Entity::id)
                                .collect(Collectors.toList()))
                        .with("include", value.getInclude())
                        .with("exclude", value.getExclude())
                        .with("promotionLevels", value.getPromotionLevels().stream()
                                .map(Entity::id)
                                .collect(Collectors.toList()))
                        .get()
        );
    }

    @Override
    public AutoPromotionProperty fromStorage(JsonNode node) {
        return loadAutoPromotionProperty(node);
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
