package net.nemerosa.ontrack.ui.controller;

import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.support.Action;

import java.net.URI;

public interface EntityURIBuilder extends URIBuilder {

    /**
     * Returns the access URI for an entity
     */
    URI getEntityURI(ProjectEntity entity);

    /**
     * Given an action with a relative URI, gets a new Action with a fully resolved URI
     */
    default Action resolveActionWithExtension(Extension extension, Action action) {
        return action.withUri(
                String.format("extension/%s/%s",
                        extension.getFeature().getId(),
                        action.getUri())
        );
    }
}
