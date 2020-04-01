package net.nemerosa.ontrack.extension.jira;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JIRAFollowLinksPropertyType extends AbstractPropertyType<JIRAFollowLinksProperty> {

    @Autowired
    public JIRAFollowLinksPropertyType(JIRAExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public String getName() {
        return "JIRA Links to follow";
    }

    @Override
    public String getDescription() {
        return "List of links to follow when displaying information about an issue.";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.PROJECT);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, JIRAFollowLinksProperty value) {
        return Form.create()
                .with(
                        MultiStrings.of("linkNames")
                                .label("Link names")
                                .value(value != null ? value.getLinkNames() : Collections.emptyList())
                );
    }

    @Override
    public JsonNode forStorage(JIRAFollowLinksProperty value) {
        return format(value);
    }

    @Override
    public JIRAFollowLinksProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public JIRAFollowLinksProperty fromStorage(JsonNode node) {
        return parse(node, JIRAFollowLinksProperty.class);
    }

    @Override
    public JIRAFollowLinksProperty replaceValue(JIRAFollowLinksProperty value, Function<String, String> replacementFunction) {
        return new JIRAFollowLinksProperty(
                value.getLinkNames().stream().map(replacementFunction).collect(Collectors.toList())
        );
    }
}
