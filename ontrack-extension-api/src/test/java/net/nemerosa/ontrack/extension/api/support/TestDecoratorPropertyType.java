package net.nemerosa.ontrack.extension.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class TestDecoratorPropertyType extends AbstractPropertyType<TestDecorationData> {

    @Autowired
    public TestDecoratorPropertyType(TestExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public String getName() {
        return "Decorator value";
    }

    @Override
    public String getDescription() {
        return "Value.";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.allOf(ProjectEntityType.class);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, ProjectEdit.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, TestDecorationData value) {
        return Form.create()
                .with(
                        Text.of("value")
                                .label("Value")
                                .value(value != null ? value.getValue() : "")
                )
                .with(
                        YesNo.of("valid")
                                .label("Valid")
                                .value(value != null && value.isValid())
                )
                ;
    }

    @Override
    public TestDecorationData fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public TestDecorationData fromStorage(JsonNode node) {
        return parse(node, TestDecorationData.class);
    }

    @Override
    public TestDecorationData replaceValue(TestDecorationData value, Function<String, String> replacementFunction) {
        return new TestDecorationData(
                replacementFunction.apply(value.getValue()),
                value.isValid()
        );
    }
}
