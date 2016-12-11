package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.NamedEntries;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.support.NameValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LinkPropertyType extends AbstractPropertyType<LinkProperty> {

    @Autowired
    public LinkPropertyType(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

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
        return securityService.isProjectFunctionGranted(entity, ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, LinkProperty value) {
        return Form.create()
                .with(
                        NamedEntries.of("links")
                                .label("List of links")
                                .nameLabel("Name")
                                .valueLabel("Link")
                                .nameOptional()
                                .addText("Add a link")
                                .help("List of links associated with a name.")
                                .value(value != null ? value.getLinks() : Collections.emptyList())
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
                        return namedLink.getValue();
                    }
                })
                .collect(Collectors.joining(","));
    }

    @Override
    public LinkProperty replaceValue(LinkProperty value, Function<String, String> replacementFunction) {
        return new LinkProperty(
                value.getLinks().stream()
                        .map(nv -> new NameValue(nv.getName(), replacementFunction.apply(nv.getValue())))
                        .collect(Collectors.toList())
        );
    }
}
