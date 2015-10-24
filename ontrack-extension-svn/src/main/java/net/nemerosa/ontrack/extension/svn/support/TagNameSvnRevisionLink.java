package net.nemerosa.ontrack.extension.svn.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.support.NoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

/**
 * Build / revision relationship based on the build name being a subversion tag.
 */
@Component
public class TagNameSvnRevisionLink extends AbstractTagBasedSvnRevisionLink<NoConfig> {

    @Autowired
    public TagNameSvnRevisionLink(SVNService svnService) {
        super(svnService);
    }

    @Override
    public String getId() {
        return "tag";
    }

    @Override
    public String getName() {
        return "Tag as name";
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
        return true;
    }

    @Override
    protected Optional<String> getTagName(NoConfig data, String buildName) {
        return Optional.of(buildName);
    }
}
