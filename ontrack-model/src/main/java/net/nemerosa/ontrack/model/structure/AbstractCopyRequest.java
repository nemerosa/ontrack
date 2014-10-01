package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

/**
 * Request for the copy of a branch configuration into another one.
 */
@Data
public abstract class AbstractCopyRequest {

    /**
     * Replacements
     */
    private final List<Replacement> replacements;

}
