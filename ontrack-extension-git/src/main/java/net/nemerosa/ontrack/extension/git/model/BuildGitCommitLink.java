package net.nemerosa.ontrack.extension.git.model;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Build;

import java.util.function.Function;

/**
 * Defines the way to link builds to Git commits, in order to manage the change logs, the Git searches
 * and synchronisations.
 *
 * @param <T> Type of configuration data
 */
public interface BuildGitCommitLink<T> {

    /**
     * ID of the link
     */
    String getId();

    /**
     * Display name for the link
     */
    String getName();

    /**
     * Clones the configuration.
     */
    T clone(T data, Function<String, String> replacementFunction);

    /**
     * For the given {@code build}, returns the corresponding Git commit
     *
     * @param build Build to get the commit for
     * @param data  Configuration of the link
     * @return Committish (short or long SHA, tag, head, etc.)
     */
    String getCommitFromBuild(Build build, T data);

    /**
     * Parses the configuration from a JSON node
     */
    T parseData(JsonNode node);

    /**
     * Formats the configuration data as JSON
     */
    JsonNode toJson(T data);

    /**
     * Creates a form for the edition of the link configuration.
     */
    Form getForm();
}
