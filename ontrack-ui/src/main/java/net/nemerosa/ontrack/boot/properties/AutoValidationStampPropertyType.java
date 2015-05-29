package net.nemerosa.ontrack.boot.properties;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class AutoValidationStampPropertyType extends AbstractPropertyType<AutoValidationStampProperty> {
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
    public Form getEditionForm(AutoValidationStampProperty value) {
        return Form.create()
                .with(
                        YesNo.of("autoCreate")
                                .label("Auto creation")
                                .help("If set, allows validation stamps to be creatdd automatically")
                                .value(value != null && value.isAutoCreate())
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
