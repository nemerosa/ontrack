package net.nemerosa.ontrack.extension.svn.model;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.svn.support.ConfiguredBuildSvnRevisionLink;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;

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

    /**
     * Getting a configured link
     */
    default <T> ConfiguredBuildSvnRevisionLink<T> getConfiguredBuildSvnRevisionLink(ServiceConfiguration serviceConfiguration) {
        return getConfiguredBuildSvnRevisionLink(
                serviceConfiguration.getId(),
                serviceConfiguration.getData()
        );
    }

    default <T> ConfiguredBuildSvnRevisionLink<T> getConfiguredBuildSvnRevisionLink(String id, JsonNode data) {
        @SuppressWarnings("unchecked")
        BuildSvnRevisionLink<T> link = (BuildSvnRevisionLink<T>) getLink(id);
        // Parses the data (for validation)
        T linkData = link.parseData(data);
        // OK
        return new ConfiguredBuildSvnRevisionLink<>(
                link,
                linkData
        );
    }
}
