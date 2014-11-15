package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;

import java.util.List;

public interface BuildGitCommitLinkService {

    /**
     * List of links
     */
    List<BuildGitCommitLink<?>> getLinks();

    /**
     * Gets a link using its ID.
     */
    BuildGitCommitLink<?> getLink(String id);

}
