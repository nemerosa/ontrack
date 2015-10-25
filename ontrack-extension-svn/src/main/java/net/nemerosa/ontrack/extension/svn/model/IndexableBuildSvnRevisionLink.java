package net.nemerosa.ontrack.extension.svn.model;

import java.util.Optional;

/**
 * {@link BuildSvnRevisionLink} which can be used to create builds from tag names.
 */
public interface IndexableBuildSvnRevisionLink<T> extends BuildSvnRevisionLink<T> {

    Optional<String> getBuildNameFromTagName(T data, String tagName);

}
