package net.nemerosa.ontrack.extension.git.model;

import java.util.Optional;

public interface IndexableBuildGitCommitLink<T> extends BuildGitCommitLink<T> {

    Optional<String> getBuildNameFromTagName(String tagName, T data);
    
}
