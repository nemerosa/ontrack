package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiForm;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.BuildConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BuildLinkPropertyType extends AbstractPropertyType<BuildLinkProperty> {

    @Autowired
    public BuildLinkPropertyType(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

    @Override
    public String getName() {
        return "Build links";
    }

    @Override
    public String getDescription() {
        return "Build links.";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BUILD);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, BuildConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, BuildLinkProperty value) {
        return Form.create()
                .with(
                        MultiForm.of(
                                "links",
                                Form.create()
                                        .with(
                                                Text.of("project").label("Project").help("Name of the project")
                                        )
                                        .with(
                                                Text.of("build").label("Build").help("Name of the build in the project")
                                        )
                        )
                                .label("Links")
                                .value(value != null ? value.getLinks() : Collections.emptyList())
                )
                ;
    }

    @Override
    public BuildLinkProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public BuildLinkProperty fromStorage(JsonNode node) {
        return parse(node, BuildLinkProperty.class);
    }

    @Override
    public String getSearchKey(BuildLinkProperty value) {
        return value.getLinks().stream()
                .map(link -> String.format("%s:%s", link.getProject(), link.getBuild()))
                .collect(Collectors.joining("|"));
    }

    @Override
    public BuildLinkProperty replaceValue(BuildLinkProperty value, Function<String, String> replacementFunction) {
        return value;
    }
}
