package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.Map;

/**
 * Request to create a template instance for one name only.
 */
@Data
public class BranchTemplateInstanceConnectRequest {

    /**
     * Branch template ID
     */
    private final int templateId;

    /**
     * Manual expressions
     */
    private final boolean manual;

    /**
     * List of parameters
     */
    private final Map<String, String> parameters;

}
