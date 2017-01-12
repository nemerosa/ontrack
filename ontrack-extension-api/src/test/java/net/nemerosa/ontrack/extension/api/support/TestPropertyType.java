package net.nemerosa.ontrack.extension.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class TestPropertyType extends AbstractPropertyType<TestProperty> implements ConfigurationPropertyType<TestConfiguration, TestProperty> {

    @Autowired
    public TestPropertyType(TestExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public String getName() {
        return "Configuration value";
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
    public Form getEditionForm(ProjectEntity entity, TestProperty value) {
        return Form.create()
                .with(
                        Text.of("value")
                                .label("Value")
                                .value(value != null ? value.getValue() : "")
                )
                ;
    }

    @Override
    public TestProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public TestProperty fromStorage(JsonNode node) {
        return parse(node, TestProperty.class);
    }

    @Override
    public String getSearchKey(TestProperty value) {
        return value.getValue();
    }

    @Override
    public TestProperty replaceValue(TestProperty value, Function<String, String> replacementFunction) {
        return new TestProperty(
                value.getConfiguration().clone(
                        replacementFunction.apply(value.getConfiguration().getName()),
                        replacementFunction
                ),
                replacementFunction.apply(value.getValue())
        );
    }
}
