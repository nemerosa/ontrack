package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.git.client.GitClient;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.exceptions.JsonParsingException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.structure.Build;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class TagPatternBuildNameGitCommitLink implements BuildGitCommitLink<TagPattern> {

    @Override
    public String getId() {
        return "tagPattern";
    }

    @Override
    public String getName() {
        return "Tag pattern";
    }

    @Override
    public TagPattern clone(TagPattern data, Function<String, String> replacementFunction) {
        return data.clone(replacementFunction);
    }

    @Override
    public String getCommitFromBuild(Build build, TagPattern data) {
        return data.getTagNameFromBuildName(build.getName())
                .orElseThrow(() -> new BuildTagPatternExcepton(data.getPattern(), build.getName()));
    }

    @Override
    public TagPattern parseData(JsonNode node) {
        try {
            return ObjectMapperFactory.create().treeToValue(node, TagPattern.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("TagPattern json", e);
        }
    }

    @Override
    public JsonNode toJson(TagPattern data) {
        return ObjectMapperFactory.create().valueToTree(data);
    }

    @Override
    public Form getForm() {
        return Form.create()
                .with(
                        Text.of("pattern")
                                .label("Tag pattern")
                                .help("@file:extension/git/help.net.nemerosa.ontrack.extension.git.support.TagPatternBuildNameGitCommitLink.tagPattern.tpl.html")
                )
                ;
    }

    @Override
    public Stream<String> getBuildCandidateReferences(String commit, GitClient gitClient, TagPattern data) {
        return gitClient.getTagsWhichContainCommit(commit).stream()
                // ... filter on valid tags only
                .filter(data::isValidTagName)
                        // ... get build names
                .map(data::getBuildNameFromTagName)
                        // ... filters on defined build names
                .filter(Optional::isPresent).map(Optional::get);
    }

    @Override
    public boolean isBuildEligible(Build build, TagPattern data) {
        return true;
    }

    @Override
    public boolean isIndexationAvailable() {
        return true;
    }
}
