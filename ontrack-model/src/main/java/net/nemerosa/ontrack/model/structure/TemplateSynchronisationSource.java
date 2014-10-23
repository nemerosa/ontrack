package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.form.Form;

import java.util.List;

/**
 * Defines where branch names come from when synchronising a template definition with
 * a list of template instances.
 *
 * @param <T> Type of configuration data
 */
public interface TemplateSynchronisationSource<T> {

    /**
     * ID of the source
     */
    String getId();

    /**
     * Display name for the source
     */
    String getName();

    /**
     * Is this source applicable for a given template branch?
     */
    boolean isApplicable(Branch branch);

    /**
     * Gets the form used for this source in a branch. This can be used
     * to get, for example, SCM information.
     */
    Form getForm(Branch branch);

    /**
     * List of branch names to use on synchronisation.
     */
    List<String> getBranchNames(Branch branch, T config);

    /**
     * Parses the configuration
     */
    T parseConfig(JsonNode node);

    /**
     * Configuration for storage
     */
    JsonNode forStorage(T config);

}
