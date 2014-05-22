package net.nemerosa.ontrack.model.structure;

/**
 * Defines a filter on builds for a branch. This is not only a predicate. It must be able to:
 * <ul>
 * <li>contribute to SQL</li>
 * <li>pre- and post- filter</li>
 * <li>acts on the number of builds</li>
 * <li>...</li>
 * </ul>
 * <p/>
 * Additionally, there must be extension points to extend the filters, at service and at GUI level.
 * <p/>
 * Typically, each extension point will either contribute to a named form, or contribute to the default form.
 */
public interface BuildFilter {
}
