package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.jenkins.client.*;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Provides a decoration that displays the state of a running job.
 */
@Component
public class JenkinsJobDecorationExtension extends AbstractExtension implements DecorationExtension {

    private final PropertyService propertyService;
    private final JenkinsClientFactory jenkinsClientFactory;

    @Autowired
    public JenkinsJobDecorationExtension(JenkinsExtensionFeature extensionFeature, PropertyService propertyService, JenkinsClientFactory jenkinsClientFactory) {
        super(extensionFeature);
        this.propertyService = propertyService;
        this.jenkinsClientFactory = jenkinsClientFactory;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(
                ProjectEntityType.PROJECT,
                ProjectEntityType.BRANCH,
                ProjectEntityType.PROMOTION_LEVEL,
                ProjectEntityType.VALIDATION_STAMP
        );
    }

    @Override
    public Decoration getDecoration(ProjectEntity entity) {
        // Gets the Jenkins Job property for this entity, if any
        Property<JenkinsJobProperty> property = propertyService.getProperty(entity, JenkinsJobPropertyType.class.getName());
        if (property.isEmpty()) {
            return null;
        } else {
            // Gets the connection information
            JenkinsConnection connection = property.getValue().getConfiguration().getConnection();
            // Gets a client
            JenkinsClient jenkinsClient = jenkinsClientFactory.getClient(connection);
            // Gets the Jenkins job
            JenkinsJob job = jenkinsClient.getJob(property.getValue().getJob(), false);
            // Gets the decoration for the job
            return getDecoration(job);
        }
    }

    private Decoration getDecoration(JenkinsJob job) {
        JenkinsJobState jenkinsJobState = job.getState();
        switch (jenkinsJobState) {
            case DISABLED:
                return Decoration.of(this, "disabled", "The Jenkins Job is disabled.");
            case RUNNING:
                // TODO Link to the running build
                return Decoration.of(this, "running", "The Jenkins Job is running.");
            case IDLE:
                return Decoration.of(this, "idle", "The Jenkins Job is not running.");
            default:
                return null;
        }
    }
}
