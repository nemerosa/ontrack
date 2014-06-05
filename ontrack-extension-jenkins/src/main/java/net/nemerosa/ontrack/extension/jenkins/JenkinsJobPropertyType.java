package net.nemerosa.ontrack.extension.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ValidationStamp;

import java.util.Optional;

public class JenkinsJobPropertyType extends AbstractJenkinsPropertyType<JenkinsJobProperty> {

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
        // FIXME Method net.nemerosa.ontrack.extension.jenkins.JenkinsJobPropertyType.forStorage
        return null;
    }

    @Override
    public JenkinsJobProperty fromStorage(JsonNode node) {
        // FIXME Method net.nemerosa.ontrack.extension.jenkins.JenkinsJobPropertyType.fromStorage
        return null;
    }
}
