package net.nemerosa.ontrack.extension.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class JenkinsJobPropertyType extends AbstractJenkinsPropertyType<JenkinsJobProperty> {

    @Autowired
    public JenkinsJobPropertyType(JenkinsExtensionFeature extensionFeature, JenkinsConfigurationService configurationService) {
        super(extensionFeature, configurationService);
    }

    @Override
    public String getName() {
        return "Jenkins Job";
    }

    @Override
    public String getDescription() {
        return "Link to a Jenkins Job";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(
                ProjectEntityType.PROJECT,
                ProjectEntityType.BRANCH,
                ProjectEntityType.PROMOTION_LEVEL,
                ProjectEntityType.VALIDATION_STAMP
        );
    }

    /**
     * Only granted for project configurators.
     */
    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity.projectId(), ProjectConfig.class);
    }

    /**
     * Everybody can see the property value.
     */
    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, JenkinsJobProperty value) {
        return super.getEditionForm(entity, value)
                .with(
                        Text.of("job")
                                .label("Job name")
                                .length(120)
                                .help("Name of Jenkins Job")
                                .value(value != null ? value.getJob() : null)
                );
    }

    @Override
    public JsonNode forStorage(JenkinsJobProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("job", value.getJob())
                        .get()
        );
    }

    @Override
    public JenkinsJobProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public JenkinsJobProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        String job = node.path("job").asText();
        // Looks the configuration up
        JenkinsConfiguration configuration = loadConfiguration(configurationName);
        // Validates the job name
        validateNotBlank(job, "The Jenkins Job name must not be empty");
        // OK
        return new JenkinsJobProperty(
                configuration,
                job
        );
    }

    @Override
    public JenkinsJobProperty replaceValue(JenkinsJobProperty value, Function<String, String> replacementFunction) {
        return new JenkinsJobProperty(
                replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getJob())
        );
    }
}
