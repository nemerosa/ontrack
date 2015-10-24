package net.nemerosa.ontrack.extension.svn.support;

import lombok.Data;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;

import java.util.OptionalLong;
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

    public boolean isValidBuildName(String name) {
        return link.isValidBuildName(data, name);
    }

    public OptionalLong getRevision(Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        return link.getRevision(data, build, branchConfigurationProperty);
    }

    public String getBuildPath(Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        return link.getBuildPath(data, build, branchConfigurationProperty);
    }
}
