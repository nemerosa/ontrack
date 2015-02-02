package net.nemerosa.ontrack.extension.scm.model;

import lombok.Data;

import java.util.List;

/**
 * Defines a filter on the file changes.
 */
@Data
public class SCMFileChangeFilter {

    /**
     * Name of the filter
     */
    private final String name;

    /**
     * List of ANT-like patterns for the paths
     */
    private final List<String> patterns;

}
