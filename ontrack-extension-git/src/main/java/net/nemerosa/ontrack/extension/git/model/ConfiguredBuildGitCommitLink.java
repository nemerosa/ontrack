package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;

import java.util.function.Function;

/**
 * Configured {@link net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink}.
 */
@Data
public class ConfiguredBuildGitCommitLink<T> {

    private final BuildGitCommitLink<T> link;
    private final T data;

    public ConfiguredBuildGitCommitLink<T> clone(Function<String, String> replacementFunction) {
        return new ConfiguredBuildGitCommitLink<>(
                link,
                link.clone(data, replacementFunction)
        );
    }

    public String getCommitFromBuild(Build build) {
        return link.getCommitFromBuild(build, data);
    }

    public boolean isBuildNameValid(String name) {
        return link.isBuildNameValid(name, data);
    }

    public ServiceConfiguration toServiceConfiguration() {
        return new ServiceConfiguration(
                link.getId(),
                link.toJson(data)
        );
    }
}
