package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LinkPropertyType extends AbstractPropertyType<LinkProperty> {

    @Override
    public String getName() {
        return "Links";
    }

    @Override
    public String getDescription() {
        return "List of links.";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.allOf(ProjectEntityType.class);
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
    public Form getEditionForm(LinkProperty value) {
        // FIXME Link property form
        return Form.create();
    }

    @Override
    public LinkProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public LinkProperty fromStorage(JsonNode node) {
        return parse(node, LinkProperty.class);
    }

    @Override
    public String getSearchKey(LinkProperty value) {
        return value.getLinks().stream()
                .map(namedLink -> {
                    if (StringUtils.isNotBlank(namedLink.getName())) {
                        return namedLink.getName();
                    } else {
                        return namedLink.getUri();
                    }
                })
                .collect(Collectors.joining(","));
    }
}
