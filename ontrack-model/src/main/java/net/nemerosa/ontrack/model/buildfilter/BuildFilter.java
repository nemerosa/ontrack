package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.structure.BuildView;

/**
 * Defines a filter on builds for a branch. This is not only a predicate. It must be able to:
 * <ul>
 * <li>contribute to the query by filtering the builds</li>
 * <li>pre- and post- filter</li>
 * <li>acts on the number of builds</li>
 * <li>...</li>
 * </ul>
 * <p>
 * Additionally, there must be extension points to extend the filters, at service and at GUI level.
 * <p>
 * Typically, each extension point will either contribute to a named form, or contribute to the default form.
 */
public interface BuildFilter {

    /**
     * Does this filter accept the given number of builds?
     * <p>
     * Implementation note: this method is called for each build.
     *
     * @param size Number of builds
     */
    boolean acceptCount(int size);

    default boolean needsBuildView() {
        return false;
    }

    default boolean acceptBuildView(BuildView buildView) {
        return true;
    }
}
