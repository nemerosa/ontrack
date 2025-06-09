package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.extension.ValidationStampPropertyType;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Component
public class AutoValidationStampPropertyType extends AbstractPropertyType<AutoValidationStampProperty>
        implements ValidationStampPropertyType<AutoValidationStampProperty> {

    private final PredefinedValidationStampService predefinedValidationStampService;
    private final SecurityService securityService;
    private final StructureService structureService;

    @Autowired
    public AutoValidationStampPropertyType(GeneralExtensionFeature extensionFeature, PredefinedValidationStampService predefinedValidationStampService, SecurityService securityService, StructureService structureService) {
        super(extensionFeature);
        this.predefinedValidationStampService = predefinedValidationStampService;
        this.securityService = securityService;
        this.structureService = structureService;
    }

    @Override
    public Optional<ValidationStamp> getOrCreateValidationStamp(AutoValidationStampProperty value, Branch branch, String validationStampName) {
        if (value.isAutoCreate()) {
            PredefinedValidationStamp oPredefinedValidationStamp = predefinedValidationStampService.findPredefinedValidationStampByName(validationStampName);
            if (oPredefinedValidationStamp != null) {
                // Creates the validation stamp
                return Optional.of(
                        securityService.asAdmin(() ->
                                structureService.newValidationStampFromPredefined(
                                        branch,
                                        oPredefinedValidationStamp
                                )
                        )
                );
            } else if (value.isAutoCreateIfNotPredefined()) {
                // Creates a validation stamp even without a predefined one
                return Optional.of(
                        securityService.asAdmin(() ->
                                structureService.newValidationStamp(
                                        ValidationStamp.of(
                                                branch,
                                                NameDescription.nd(validationStampName, "Validation automatically created on demand.")
                                        )
                                )
                        )
                );
            }
        }
        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Auto validation stamps";
    }

    @Override
    public String getDescription() {
        return "If set, this property allows validation stamps to be created automatically from predefined validation stamps";
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
    public AutoValidationStampProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public AutoValidationStampProperty fromStorage(JsonNode node) {
        return parse(node, AutoValidationStampProperty.class);
    }

    @Override
    public AutoValidationStampProperty replaceValue(@NotNull AutoValidationStampProperty value, Function<String, String> replacementFunction) {
        return value;
    }
}
