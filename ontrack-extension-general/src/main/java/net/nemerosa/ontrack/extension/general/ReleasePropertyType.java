package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.PromotionRunCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertySearchArguments;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class ReleasePropertyType extends AbstractPropertyType<ReleaseProperty> {

    @Autowired
    public ReleasePropertyType(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

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

    /**
     * If one can promote a build, he can also attach a release label to a build.
     */
    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, PromotionRunCreate.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, ReleaseProperty value) {
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

    @Override
    public boolean containsValue(ReleaseProperty value, String propertyValue) {
        return StringUtils.containsIgnoreCase(value.getName(), propertyValue);
    }

    @Nullable
    @Override
    public PropertySearchArguments getSearchArguments(String token) {
        if (StringUtils.isNotBlank(token) && token.length() > 1) {
            return new PropertySearchArguments(
                    null,
                    "pp.json->>'name' ilike :token",
                    ImmutableMap.of("token", "%" + token + "%")
            );
        } else {
            return null;
        }
    }
}
