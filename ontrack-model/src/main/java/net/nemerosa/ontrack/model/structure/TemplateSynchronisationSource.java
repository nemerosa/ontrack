package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.form.Form;

import java.util.List;

/**
 * Defines where branch names come from when synchronising a template definition with
 * a list of template instances.
 */
public interface TemplateSynchronisationSource {

    /**
     * ID of the source
     */
    String getId();

    /**
     * Display name for the source
     */
    String getName();

    /**
     * Is this source applicable for a given project?
     */
    boolean isApplicable(Project project);

    /**
     * Gets the form used for this source in a project. This can be used
     * to get, for example, SCM information.
     */
    Form getForm(Project project);

    /**
     * List of branch names to use on synchronisation.
     */
    List<String> getBranchNames(Project project);

}
