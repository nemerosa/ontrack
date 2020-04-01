package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.extension.PromotionLevelPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Component
public class AutoPromotionLevelPropertyType extends AbstractPropertyType<AutoPromotionLevelProperty>
        implements PromotionLevelPropertyType<AutoPromotionLevelProperty> {

    private final PredefinedPromotionLevelService predefinedPromotionLevelService;
    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public AutoPromotionLevelPropertyType(
            GeneralExtensionFeature extensionFeature,
            PredefinedPromotionLevelService predefinedPromotionLevelService,
            StructureService structureService,
            SecurityService securityService
    ) {
        super(extensionFeature);
        this.predefinedPromotionLevelService = predefinedPromotionLevelService;
        this.structureService = structureService;
        this.securityService = securityService;
    }

    @Override
    public Optional<PromotionLevel> getOrCreatePromotionLevel(AutoPromotionLevelProperty value, Branch branch, String promotionLevelName) {
        if (value.isAutoCreate()) {
            Optional<PredefinedPromotionLevel> oPredefinedPromotionLevel = predefinedPromotionLevelService.findPredefinedPromotionLevelByName(promotionLevelName);
            if (oPredefinedPromotionLevel.isPresent()) {
                // Creates the promotion level
                return Optional.of(
                        securityService.asAdmin(() ->
                                        structureService.newPromotionLevelFromPredefined(
                                                branch,
                                                oPredefinedPromotionLevel.get()
                                        )
                        )
                );
            }
        }
        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Auto promotion levels";
    }

    @Override
    public String getDescription() {
        return "If set, this property allows promotion levels to be created automatically from predefined promotion levels";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.PROJECT);
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
    public Form getEditionForm(ProjectEntity entity, AutoPromotionLevelProperty value) {
        return Form.create()
                .with(
                        YesNo.of("autoCreate")
                                .label("Auto creation")
                                .help("If set, allows promotion levels to be created automatically")
                                .value(value != null && value.isAutoCreate())
                )
                ;
    }

    @Override
    public AutoPromotionLevelProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public AutoPromotionLevelProperty fromStorage(JsonNode node) {
        return parse(node, AutoPromotionLevelProperty.class);
    }

    @Override
    public AutoPromotionLevelProperty replaceValue(AutoPromotionLevelProperty value, Function<String, String> replacementFunction) {
        return value;
    }
}
