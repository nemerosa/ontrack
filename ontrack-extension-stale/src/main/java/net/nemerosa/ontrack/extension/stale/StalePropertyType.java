package net.nemerosa.ontrack.extension.stale;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class StalePropertyType extends AbstractPropertyType<StaleProperty> {

    @Override
    public String getName() {
        return "Stale branches";
    }

    @Override
    public String getDescription() {
        return "Allows to disable or delete stale branches";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.PROJECT);
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
    public Form getEditionForm(ProjectEntity entity, StaleProperty value) {
        return Form.create()
                .with(
                        Int.of("disablingDuration")
                                .label("Disabling branches after N (days)")
                                .min(0)
                                .help("Number of days of inactivity after a branch is disabled. 0 means that " +
                                        "the branch won't ever be disabled automatically.")
                                .value(value != null ? value.getDisablingDuration() : 0)
                )
                .with(
                        Int.of("deletingDuration")
                                .label("Deleting branches after N (days) more")
                                .min(0)
                                .help("Number of days of inactivity after a branch is deleted, after it has been" +
                                        "disabled automatically. 0 means that " +
                                        "the branch won't ever be deleted automatically.")
                                .value(value != null ? value.getDeletingDuration() : 0)
                )
                ;
    }

    @Override
    public StaleProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public StaleProperty fromStorage(JsonNode node) {
        return parse(node, StaleProperty.class);
    }

    @Override
    public String getSearchKey(StaleProperty value) {
        return null;
    }

    @Override
    public StaleProperty replaceValue(StaleProperty value, Function<String, String> replacementFunction) {
        return value;
    }
}
