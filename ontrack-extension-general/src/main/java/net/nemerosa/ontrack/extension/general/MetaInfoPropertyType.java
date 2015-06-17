package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiForm;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetaInfoPropertyType extends AbstractPropertyType<MetaInfoProperty> {

    @Override
    public String getName() {
        return "Meta information";
    }

    @Override
    public String getDescription() {
        return "List of meta information properties";
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
    public Form getEditionForm(ProjectEntity entity, MetaInfoProperty value) {
        return Form.create()
                .with(
                        MultiForm.of(
                                "items",
                                Form.create()
                                        .name()
                                        .with(
                                                Text.of("value").label("Value")
                                        )
                                        .with(
                                                Text.of("link").label("Link").optional()
                                        )
                        )
                                .label("Items")
                                .value(value != null ? value.getItems() : Collections.emptyList())
                )
                ;
    }

    @Override
    public MetaInfoProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public MetaInfoProperty fromStorage(JsonNode node) {
        return parse(node, MetaInfoProperty.class);
    }

    @Override
    public String getSearchKey(MetaInfoProperty value) {
        return value.getItems().stream()
                .map(
                        item -> String.format("%s->%s", item.getName(), item.getValue())
                )
                .collect(Collectors.joining(";"));
    }

    @Override
    public MetaInfoProperty replaceValue(MetaInfoProperty value, Function<String, String> replacementFunction) {
        return new MetaInfoProperty(
                value.getItems().stream()
                        .map(
                                item -> new MetaInfoPropertyItem(
                                        item.getName(),
                                        replacementFunction.apply(item.getValue()),
                                        replacementFunction.apply(item.getLink())
                                )
                        )
                        .collect(Collectors.toList())
        );
    }
}
