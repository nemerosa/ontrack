package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.extension.ValidationStampPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.structure.*;
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
            Optional<PredefinedValidationStamp> oPredefinedValidationStamp = predefinedValidationStampService.findPredefinedValidationStampByName(validationStampName);
            if (oPredefinedValidationStamp.isPresent()) {
                // Creates the validation stamp
                return Optional.of(
                        securityService.asAdmin(() ->
                                structureService.newValidationStampFromPredefined(
                                        branch,
                                        oPredefinedValidationStamp.get()
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
    public Form getEditionForm(ProjectEntity entity, AutoValidationStampProperty value) {
        return Form.create()
                .with(
                        YesNo.of("autoCreate")
                                .label("Auto creation")
                                .help("If set, allows validation stamps to be created automatically")
                                .value(value != null && value.isAutoCreate())
                )
                .with(
                        YesNo.of("autoCreateIfNotPredefined")
                                .label("Auto creation if not predefined")
                                .help("If set, allows validation stamps to be created automatically, even if not predefined version is present.")
                                .value(value != null && value.isAutoCreateIfNotPredefined())
                )
                ;
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
    public String getSearchKey(AutoValidationStampProperty value) {
        return "";
    }

    @Override
    public AutoValidationStampProperty replaceValue(AutoValidationStampProperty value, Function<String, String> replacementFunction) {
        return value;
    }
}
