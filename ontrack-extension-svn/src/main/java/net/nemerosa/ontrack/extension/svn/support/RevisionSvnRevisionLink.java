package net.nemerosa.ontrack.extension.svn.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.support.NoConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.OptionalLong;
import java.util.function.Function;

/**
 * Build / revision relationship based on the build name being a subversion revision.
 */
@Component
public class RevisionSvnRevisionLink implements BuildSvnRevisionLink<NoConfig> {

    @Override
    public String getId() {
        return "revision";
    }

    @Override
    public String getName() {
        return "Revision as name";
    }

    @Override
    public NoConfig clone(NoConfig data, Function<String, String> replacementFunction) {
        return data;
    }

    @Override
    public NoConfig parseData(JsonNode node) {
        return NoConfig.INSTANCE;
    }

    @Override
    public JsonNode toJson(NoConfig data) {
        return JsonUtils.object().end();
    }

    @Override
    public Form getForm() {
        return Form.create();
    }

    @Override
    public boolean isValidBuildName(NoConfig data, String name) {
        return StringUtils.isNumeric(name);
    }

    @Override
    public OptionalLong getRevision(NoConfig data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        if (StringUtils.isNumeric(build.getName())) {
            return OptionalLong.of(Long.parseLong(build.getName(), 10));
        } else {
            return OptionalLong.empty();
        }
    }
}
