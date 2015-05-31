package net.nemerosa.ontrack.extension.git.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class GitCommitPropertyType extends AbstractPropertyType<GitCommitProperty> {

    @Override
    public String getName() {
        return "Git commit";
    }

    @Override
    public String getDescription() {
        return "Git commit";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.BUILD);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, BuildCreate.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, GitCommitProperty value) {
        return Form.create()
                .with(
                        Text.of("commit")
                                .label("Git commit")
                                .value(value != null ? value.getCommit() : "HEAD")
                );
    }

    @Override
    public GitCommitProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public GitCommitProperty fromStorage(JsonNode node) {
        return new GitCommitProperty(
                JsonUtils.get(node, "commit")
        );
    }

    @Override
    public String getSearchKey(GitCommitProperty value) {
        return value.getCommit();
    }

    @Override
    public GitCommitProperty replaceValue(GitCommitProperty value, Function<String, String> replacementFunction) {
        // A commit is immutable...
        return value;
    }

}
