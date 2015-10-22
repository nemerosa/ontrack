package net.nemerosa.ontrack.extension.svn.support;

import lombok.Data;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;

import java.util.function.Function;

/**
 * Configured {@link net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink}.
 */
@Data
public class ConfiguredBuildSvnRevisionLink<T> {

    private final BuildSvnRevisionLink<T> link;
    private final T data;

    public ConfiguredBuildSvnRevisionLink<T> clone(Function<String, String> replacementFunction) {
        return new ConfiguredBuildSvnRevisionLink<>(
                link,
                link.clone(data, replacementFunction)
        );
    }

    public ServiceConfiguration toServiceConfiguration() {
        return new ServiceConfiguration(
                link.getId(),
                link.toJson(data)
        );
    }
}
