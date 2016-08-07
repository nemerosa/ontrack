package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.MapBuilder;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class SVNSyncPropertyType extends AbstractPropertyType<SVNSyncProperty> {

    private final PropertyService propertyService;

    @Autowired
    public SVNSyncPropertyType(SVNExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public String getName() {
        return "SVN synchronisation";
    }

    @Override
    public String getDescription() {
        return "Allows the synchronisation of the builds with the tags in Subversion.";
    }

    /**
     * Only at branch level.
     */
    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BRANCH);
    }

    /**
     * One can edit the SVN synchronisation only if he can configure the project and if the branch
     * is configured for SVN.
     */
    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig.class) &&
                propertyService.hasProperty(
                        entity,
                        SVNBranchConfigurationPropertyType.class);
    }

    /**
     * Only the project configurator can see the property. The other ones do not have to bother.
     */
    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig.class);
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, SVNSyncProperty value) {
        return Form.create()
                .with(
                        YesNo.of("override")
                                .label("Override builds")
                                .help("Can the existing builds be overridden by a synchronisation? If yes, " +
                                        "the existing validation and promotion runs would be lost as well.")
                                .value(value != null && value.isOverride())
                )
                .with(
                        Int.of("interval")
                                .label("Sync. interval (min)")
                                .min(0)
                                .max(60 * 24 * 7) // 1 week
                                .help("Interval in minutes for the synchronisation. If 0, the synchronisation " +
                                        "must be done manually")
                                .value(value != null ? value.getInterval() : 0)
                );
    }

    @Override
    public JsonNode forStorage(SVNSyncProperty value) {
        return format(
                MapBuilder.params()
                        .with("override", value.isOverride())
                        .with("interval", value.getInterval())
                        .get()
        );
    }

    @Override
    public SVNSyncProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public SVNSyncProperty fromStorage(JsonNode node) {
        boolean override = node.path("override").asBoolean();
        int interval = node.path("interval").asInt();
        // Validates the interval
        if (interval < 0) interval = 0;
        // OK
        return new SVNSyncProperty(
                override,
                interval
        );
    }

    @Override
    public String getSearchKey(SVNSyncProperty value) {
        return "";
    }

    @Override
    public SVNSyncProperty replaceValue(SVNSyncProperty value, Function<String, String> replacementFunction) {
        return value;
    }

}
