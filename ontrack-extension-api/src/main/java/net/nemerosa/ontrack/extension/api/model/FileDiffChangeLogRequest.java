package net.nemerosa.ontrack.extension.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

/**
 * Request for the export of a diff from a change log.
 * <p>
 * Additionally to the {@linkplain BuildDiffRequest change log request},
 * this request defines a filter to apply on the file list, using a list of ANT-like patterns.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class FileDiffChangeLogRequest extends BuildDiffRequest {

    /**
     * List of ANT-like formats to apply on the paths
     */
    private List<String> patterns = Collections.emptyList();

}
