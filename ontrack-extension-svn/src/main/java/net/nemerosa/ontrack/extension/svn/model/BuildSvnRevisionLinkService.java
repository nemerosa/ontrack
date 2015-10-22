package net.nemerosa.ontrack.extension.svn.model;

import java.util.List;
import java.util.Optional;

public interface BuildSvnRevisionLinkService {

    /**
     * List of links
     */
    List<BuildSvnRevisionLink<?>> getLinks();

    /**
     * Gets a link using its ID.
     */
    Optional<BuildSvnRevisionLink<?>> getOptionalLink(String id);

    /**
     * Getting a link using its ID
     */
    default BuildSvnRevisionLink<?> getLink(String id) {
        return getOptionalLink(id).orElseThrow(() -> new BuildSvnRevisionLinkNotFoundException(id));
    }
}
