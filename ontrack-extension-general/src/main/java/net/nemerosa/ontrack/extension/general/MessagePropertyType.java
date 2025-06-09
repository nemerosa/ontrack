package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertySearchArguments;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class MessagePropertyType extends AbstractPropertyType<MessageProperty> {

    @Autowired
    public MessagePropertyType(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public String getName() {
        return "Message";
    }

    @Override
    public String getDescription() {
        return "Associates an arbitrary message (and its type) to an entity. Will be displayed as a decorator in the UI.";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.allOf(ProjectEntityType.class);
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
    public MessageProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public MessageProperty fromStorage(JsonNode node) {
        return parse(node, MessageProperty.class);
    }

    @Override
    public MessageProperty replaceValue(@NotNull MessageProperty value, Function<String, String> replacementFunction) {
        return new MessageProperty(
                value.getType(),
                replacementFunction.apply(value.getText())
        );
    }

    @Nullable
    @Override
    public PropertySearchArguments getSearchArguments(String token) {
        return new PropertySearchArguments(
                null,
                "pp.json->>'text' ilike :text",
                Collections.singletonMap("text", "%" + token + "%")
        );
    }

    @Override
    public boolean containsValue(MessageProperty value, String propertyValue) {
        return StringUtils.containsIgnoreCase(value.getText(), propertyValue);
    }
}
