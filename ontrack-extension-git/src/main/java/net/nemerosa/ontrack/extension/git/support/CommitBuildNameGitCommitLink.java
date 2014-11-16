package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.git.client.GitClient;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.structure.Build;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class CommitBuildNameGitCommitLink implements BuildGitCommitLink<CommitLinkConfig> {

    @Override
    public String getId() {
        return "commit";
    }

    @Override
    public String getName() {
        return "Commit as name";
    }

    @Override
    public CommitLinkConfig clone(CommitLinkConfig data, Function<String, String> replacementFunction) {
        return data;
    }

    @Override
    public String getCommitFromBuild(Build build, CommitLinkConfig data) {
        return build.getName();
    }

    @Override
    public CommitLinkConfig parseData(JsonNode node) {
        try {
            return ObjectMapperFactory.create().treeToValue(node, CommitLinkConfig.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("CommitLinkConfig json", e);
        }
    }

    @Override
    public JsonNode toJson(CommitLinkConfig data) {
        return ObjectMapperFactory.create().valueToTree(data);
    }

    @Override
    public Form getForm() {
        return Form.create()
                .with(
                        YesNo.of("abbreviated")
                                .label("Abbreviated")
                                .help("Using abbreviated commit hashes or not.")
                                .value(true)
                );
    }

    @Override
    public Stream<String> getBuildCandidateReferences(String commit, GitClient gitClient, CommitLinkConfig data) {
        return gitClient.rawLog(
                String.format("%s~1", commit),
                "HEAD"
        )
                .sorted()
                .map(
                        gitCommit -> data.isAbbreviated() ?
                                gitCommit.getShortId() :
                                gitCommit.getId()
                );
    }

    @Override
    public boolean isBuildEligible(Build build, CommitLinkConfig data) {
        return true;
    }

    /**
     * No indexation is possible for links based on commits.
     */
    @Override
    public boolean isIndexationAvailable() {
        return false;
    }
}
