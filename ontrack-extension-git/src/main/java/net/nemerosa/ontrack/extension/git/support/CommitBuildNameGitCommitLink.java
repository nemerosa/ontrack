package net.nemerosa.ontrack.extension.git.support;

import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import org.springframework.stereotype.Component;

@Component
public class CommitBuildNameGitCommitLink implements BuildGitCommitLink<NoConfig> {

    @Override
    public String getId() {
        return "commit";
    }

    @Override
    public String getName() {
        return "Commit";
    }

}
