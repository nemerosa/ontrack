package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Memo;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Describable;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessagePropertyType extends AbstractPropertyType<MessageProperty> {

    @Override
    public String getName() {
        return "Message";
    }

    @Override
    public String getDescription() {
        return "Message.";
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
    public Form getEditionForm(ProjectEntity entity, MessageProperty value) {
        return Form.create()
                .with(
                        Selection.of("type")
                                .label("Type")
                                .help("Type of message")
                                .items(
                                        Arrays.asList(MessageType.values()).stream()
                                                .map(Describable::toDescription)
                                                .collect(Collectors.toList())
                                )
                                .itemId("id")
                                .itemName("name")
                                .value(
                                        value != null ? value.getType().getId() : MessageType.INFO.getId()
                                )
                )
                .with(
                        Memo.of("text")
                                .label("Text")
                                .value(value != null ? value.getText() : "")
                )
                ;
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
    public String getSearchKey(MessageProperty value) {
        return value.getText();
    }

    @Override
    public MessageProperty replaceValue(MessageProperty value, Function<String, String> replacementFunction) {
        return new MessageProperty(
                value.getType(),
                replacementFunction.apply(value.getText())
        );
    }
}
