package net.nemerosa.ontrack.extension.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ValidationStamp;

import java.util.Optional;

public class JenkinsJobPropertyType extends AbstractJenkinsPropertyType<JenkinsJobProperty> {

    private final JenkinsConfigurationService configurationService;

    public JenkinsJobPropertyType(JenkinsConfigurationService configurationService) {
        this.configurationService = configurationService;
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
    public String getIconPath() {
        return "assets/extension/jenkins/JenkinsJob.png";
    }

    @Override
    public String getShortTemplatePath() {
        return "app/extension/jenkins/jenkins-job-property-short.html";
    }

    @Override
    public String getFullTemplatePath() {
        return "app/extension/jenkins/jenkins-job-property-full.html";
    }

    @Override
    public boolean applies(Class<? extends ProjectEntity> entityClass) {
        return entityClass.isAssignableFrom(Project.class)
                || entityClass.isAssignableFrom(Branch.class)
                || entityClass.isAssignableFrom(ValidationStamp.class);
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
    public Form getEditionForm(Optional<JenkinsJobProperty> value) {
        // FIXME Method net.nemerosa.ontrack.extension.jenkins.JenkinsJobPropertyType.getEditionForm
        return null;
    }

    @Override
    protected void validate(JenkinsJobProperty value) {
        // FIXME Method net.nemerosa.ontrack.extension.jenkins.JenkinsJobPropertyType.validate

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
    public JenkinsJobProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        String job = node.path("job").asText();
        JenkinsConfiguration configuration = configurationService.getConfiguration(configurationName);
        return new JenkinsJobProperty(
                configuration,
                job
        );
    }
}
