package net.nemerosa.ontrack.extension.stash.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.git.property.AbstractGitProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.extension.stash.StashExtensionFeature;
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class StashProjectConfigurationPropertyType extends AbstractGitProjectConfigurationPropertyType<StashProjectConfigurationProperty>
        implements ConfigurationPropertyType<StashConfiguration, StashProjectConfigurationProperty> {

    private final StashConfigurationService configurationService;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public StashProjectConfigurationPropertyType(StashExtensionFeature extensionFeature, StashConfigurationService configurationService, IssueServiceRegistry issueServiceRegistry) {
        super(extensionFeature);
        this.configurationService = configurationService;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public String getName() {
        return "BitBucket configuration";
    }

    @Override
    public String getDescription() {
        return "Associates the project with a BitBucket repository";
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
    public Form getEditionForm(ProjectEntity entity, StashProjectConfigurationProperty value) {
        List<IssueServiceConfigurationRepresentation> availableIssueServiceConfigurations = issueServiceRegistry.getAvailableIssueServiceConfigurations();
        return Form.create()
                .with(
                        Selection.of("configuration")
                                .label("Configuration")
                                .help("BitBucket configuration to use to access the repository")
                                .items(configurationService.getConfigurationDescriptors())
                                .value(value != null ? value.getConfiguration().getName() : null)
                )
                .with(
                        Text.of("project")
                                .label("Project")
                                .help("ID of the BitBucket project")
                                .value(value != null ? value.getProject() : null)
                )
                .with(
                        Text.of("repository")
                                .label("Repository")
                                .help("Repository in the BitBucket project")
                                .value(value != null ? value.getRepository() : null)
                )
                .with(
                        Int.of("indexationInterval")
                                .label("Indexation interval")
                                .min(0)
                                .max(60 * 24)
                                .value(value != null ? value.getIndexationInterval() : 0)
                                .help("@file:extension/git/help.net.nemerosa.ontrack.extension.git.model.GitConfiguration.indexationInterval.tpl.html")
                )
                .with(
                        Selection.of("issueServiceConfigurationIdentifier")
                                .label("Issue configuration")
                                .help("Select an issue service that is sued to associate tickets and issues to the source.")
                                .optional()
                                .items(availableIssueServiceConfigurations)
                                .value(value != null ? value.getIssueServiceConfigurationIdentifier() : "")
                )
                ;
    }

    @Override
    public StashProjectConfigurationProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public StashProjectConfigurationProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        // Looks the configuration up
        StashConfiguration configuration = configurationService.getConfiguration(configurationName);
        // OK
        return new StashProjectConfigurationProperty(
                configuration,
                node.path("project").asText(),
                node.path("repository").asText(),
                node.path("indexationInterval").asInt(),
                node.path("issueServiceConfigurationIdentifier").asText()
        );
    }

    @Override
    public JsonNode forStorage(StashProjectConfigurationProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("project", value.getProject())
                        .with("repository", value.getRepository())
                        .with("indexationInterval", value.getIndexationInterval())
                        .with("issueServiceConfigurationIdentifier", value.getIssueServiceConfigurationIdentifier())
                        .get()
        );
    }

    @Override
    public String getSearchKey(StashProjectConfigurationProperty value) {
        return value.getConfiguration().getName();
    }

    @Override
    public StashProjectConfigurationProperty replaceValue(StashProjectConfigurationProperty value, Function<String, String> replacementFunction) {
        return new StashProjectConfigurationProperty(
                configurationService.replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getProject()),
                replacementFunction.apply(value.getRepository()),
                value.getIndexationInterval(),
                value.getIssueServiceConfigurationIdentifier()
        );
    }

}
