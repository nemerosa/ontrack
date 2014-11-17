package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLinkNotFoundException;

import java.util.List;
import java.util.Optional;

public interface BuildGitCommitLinkService {

    /**
     * List of links
     */
    List<BuildGitCommitLink<?>> getLinks();

    /**
     * Gets a link using its ID.
     */
    Optional<BuildGitCommitLink<?>> getOptionalLink(String id);

    default BuildGitCommitLink<?> getLink(String id) {
        return getOptionalLink(id).orElseThrow(() -> new BuildGitCommitLinkNotFoundException(id));
    }

}
