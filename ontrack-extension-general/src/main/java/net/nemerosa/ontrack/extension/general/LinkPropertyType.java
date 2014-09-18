package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.NamedEntries;
import net.nemerosa.ontrack.model.security.ProjectEdit;
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
        return securityService.isProjectFunctionGranted(entity, ProjectEdit.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(LinkProperty value) {
        return Form.create()
                .with(
                        NamedEntries.of("links")
                                .label("List of links")
                                .nameLabel("Name")
                                .nameOptional()
                                .addText("Add a link")
                                .help("List of links")
                )
                ;
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
