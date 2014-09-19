package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class ReleasePropertyType extends AbstractPropertyType<ReleaseProperty> {

    @Override
    public String getName() {
        return "Release";
    }

    @Override
    public String getDescription() {
        return "Release indicator on the build.";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BUILD);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, BuildCreate.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ReleaseProperty value) {
        return Form.create()
                .with(
                        Text.of("name")
                                .label("Release name")
                                .length(20)
                                .value(value != null ? value.getName() : null)
                );
    }

    @Override
    public ReleaseProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public ReleaseProperty fromStorage(JsonNode node) {
        return new ReleaseProperty(
                node.path("name").asText()
        );
    }

    @Override
    public String getSearchKey(ReleaseProperty value) {
        return value.getName();
    }

    @Override
    public ReleaseProperty replaceValue(ReleaseProperty value, Function<String, String> replacementFunction) {
        return value;
    }
}
