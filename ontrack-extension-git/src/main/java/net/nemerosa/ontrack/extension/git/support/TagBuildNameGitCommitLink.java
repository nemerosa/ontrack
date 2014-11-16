package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.git.client.GitClient;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Build;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class TagBuildNameGitCommitLink implements BuildGitCommitLink<NoConfig> {

    /**
     * Available as default
     */
    public static final ConfiguredBuildGitCommitLink<NoConfig> DEFAULT = new ConfiguredBuildGitCommitLink<>(
            new TagBuildNameGitCommitLink(),
            NoConfig.INSTANCE
    );

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
    public String getCommitFromBuild(Build build, NoConfig data) {
        return build.getName();
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

    /**
     * Returns all tags starting from the {@code commit}.
     */
    @Override
    public Stream<String> getBuildCandidateReferences(String commit, GitClient gitClient, NoConfig data) {
        return gitClient.getTagsWhichContainCommit(commit).stream();
    }

    @Override
    public boolean isBuildEligible(Build build, NoConfig data) {
        return true;
    }

    @Override
    public boolean isIndexationAvailable() {
        return true;
    }
}
