package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;

import java.util.List;

/**
 * {@link BuildFilterProvider} with its associated data.
 *
 * @param <T> Type of date for the filter provider
 */
@Data
public class BuildFilterProviderData<T> {

    /**
     * Provider service
     */
    private final BuildFilterProvider<T> provider;

    /**
     * Data
     */
    private final T data;

    /**
     * Builder
     */
    public static <T> BuildFilterProviderData<T> of(BuildFilterProvider<T> provider, T data) {
        return new BuildFilterProviderData<>(provider, data);
    }

    /**
     * Launches the filter
     */
    public List<Build> filterBranchBuilds(Branch branch) {
        return provider.filterBranchBuilds(branch, data);
    }
}
