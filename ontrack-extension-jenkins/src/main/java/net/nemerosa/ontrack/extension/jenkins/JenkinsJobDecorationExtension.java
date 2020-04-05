package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient;
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory;
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsJob;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Provides a decoration that displays the state of a running job.
 */
@Component
public class JenkinsJobDecorationExtension extends AbstractExtension implements DecorationExtension<JenkinsJob> {

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
    public List<Decoration<JenkinsJob>> getDecorations(ProjectEntity entity) {
        // Gets the Jenkins Job property for this entity, if any
        Property<JenkinsJobProperty> property = propertyService.getProperty(entity, JenkinsJobPropertyType.class.getName());
        if (property.isEmpty()) {
            return Collections.emptyList();
        } else {
            // Gets a client
            // FIXME getJob does not need a full HTTP client
            JenkinsClient jenkinsClient = jenkinsClientFactory.getClient(property.getValue().getConfiguration());
            // Gets the Jenkins job
            JenkinsJob job = jenkinsClient.getJob(property.getValue().getJob());
            // Gets the decoration for the job
            return Collections.singletonList(
                    getDecoration(job)
            );
        }
    }

    private Decoration<JenkinsJob> getDecoration(JenkinsJob job) {
        return Decoration.of(this, job);
    }

}
