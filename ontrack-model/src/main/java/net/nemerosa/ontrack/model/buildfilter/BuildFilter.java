package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildView;

import java.util.List;
import java.util.function.Supplier;

/**
 * Defines a filter on builds for a branch.
 * <p>
 * Implementation note: a <code>BuildFilter</code> instance is created for each query, and can therefore
 * hold instance variables.
 */
@Deprecated
public interface BuildFilter {

    /**
     * Actual filter method that is called to filter the build.
     *
     * @param builds            Current list of builds as they have been already accepted by the filter. This filter
     *                          is free to update this list if needed.
     * @param branch            Branch the filter operates on
     * @param build             Build to filter
     * @param buildViewSupplier If the filter needs to access the
     *                          {@link net.nemerosa.ontrack.model.structure.BuildView BuildView}
     *                          for this build, it can call this supplier to load it on demand.
     * @return Result of the filter. The {@link BuildFilterResult#goingOn goingOn} property must be set to
     * <code>true</code> if the filtering must go on with following builds. The
     * {@link BuildFilterResult#accept accept} property must be set to
     * <code>true</code> if the build must be added to the list (note that the filter could add it directly to
     * the list and return <code>false</code> instead)
     */
    BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier);

    /**
     * Initialises this filter so it can be used. By default, this does nothing.
     *
     * @param branch Branch to use for the filter
     */
    default void init(Branch branch) {
    }
}
