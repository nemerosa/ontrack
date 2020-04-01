package net.nemerosa.ontrack.extension.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.model.exceptions.PropertyUnsupportedEntityTypeException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.PromotionRunCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.security.ValidationRunCreate;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class JenkinsBuildPropertyType extends AbstractJenkinsPropertyType<JenkinsBuildProperty> {

    @Autowired
    public JenkinsBuildPropertyType(JenkinsExtensionFeature extensionFeature, JenkinsConfigurationService configurationService) {
        super(extensionFeature, configurationService);
    }

    @Override
    public String getName() {
        return "Jenkins Build";
    }

    @Override
    public String getDescription() {
        return "Link to a Jenkins Build";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(
                ProjectEntityType.BUILD,
                ProjectEntityType.PROMOTION_RUN,
                ProjectEntityType.VALIDATION_RUN
        );
    }

    /**
     * Depends on the nature of the entity. Allowed to the ones who can create the associated entity.
     */
    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        switch (entity.getProjectEntityType()) {
            case BUILD:
                return securityService.isProjectFunctionGranted(entity, BuildCreate.class);
            case PROMOTION_RUN:
                return securityService.isProjectFunctionGranted(entity, PromotionRunCreate.class);
            case VALIDATION_RUN:
                return securityService.isProjectFunctionGranted(entity, ValidationRunCreate.class);
            default:
                throw new PropertyUnsupportedEntityTypeException(getClass().getName(), entity.getProjectEntityType());
        }
    }

    /**
     * Everybody can see the property value.
     */
    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, JenkinsBuildProperty value) {
        return super.getEditionForm(entity, value)
                .with(
                        Text.of("job")
                                .label("Job name")
                                .length(120)
                                .help("Name of Jenkins Job")
                                .value(value != null ? value.getJob() : null)
                )
                .with(
                        Int.of("build")
                                .label("Build number")
                                .min(1)
                                .help("Jenkins Build number")
                                .value(value != null ? value.getBuild() : null)
                );
    }

    @Override
    public JsonNode forStorage(JenkinsBuildProperty value) {
        return format(
                MapBuilder.params()
                        .with("configuration", value.getConfiguration().getName())
                        .with("job", value.getJob())
                        .with("build", value.getBuild())
                        .get()
        );
    }

    @Override
    public JenkinsBuildProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public JenkinsBuildProperty fromStorage(JsonNode node) {
        String configurationName = node.path("configuration").asText();
        String job = node.path("job").asText();
        int build = node.path("build").asInt();
        // Looks the configuration up
        JenkinsConfiguration configuration = loadConfiguration(configurationName);
        // Validates the job name
        validateNotBlank(job, "The Jenkins Job name must not be empty");
        // OK
        return new JenkinsBuildProperty(
                configuration,
                job,
                build
        );
    }

    @Override
    public JenkinsBuildProperty replaceValue(JenkinsBuildProperty value, Function<String, String> replacementFunction) {
        return new JenkinsBuildProperty(
                replaceConfiguration(value.getConfiguration(), replacementFunction),
                replacementFunction.apply(value.getJob()),
                value.getBuild()
        );
    }
}
