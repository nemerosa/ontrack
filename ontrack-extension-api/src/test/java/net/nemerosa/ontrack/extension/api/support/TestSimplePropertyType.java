package net.nemerosa.ontrack.extension.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
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
public class TestSimplePropertyType extends AbstractPropertyType<TestSimpleProperty> {

    @Autowired
    public TestSimplePropertyType(TestExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public String getName() {
        return "Simple value";
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
    public Form getEditionForm(ProjectEntity entity, TestSimpleProperty value) {
        return Form.create()
                .with(
                        Text.of("value")
                                .label("Value")
                                .value(value != null ? value.getValue() : "")
                )
                ;
    }

    @Override
    public TestSimpleProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public TestSimpleProperty fromStorage(JsonNode node) {
        return parse(node, TestSimpleProperty.class);
    }

    @Override
    public String getSearchKey(TestSimpleProperty value) {
        return value.getValue();
    }

    @Override
    public TestSimpleProperty replaceValue(TestSimpleProperty value, Function<String, String> replacementFunction) {
        return new TestSimpleProperty(
                replacementFunction.apply(value.getValue())
        );
    }
}
