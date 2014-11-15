package net.nemerosa.ontrack.extension.git.support;

import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink;
import org.springframework.stereotype.Component;

import java.util.function.Function;

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

}
