package net.nemerosa.ontrack.docs;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Documentation for a method
 */
@Data
public class DSLDocMethod {

    /**
     * ID of the method
     */
    private final String id;

    /**
     * Name of the method
     */
    private final String name;

    /**
     * Signature of the method
     */
    private final String signature;

    /**
     * Description
     */
    private final String description;

    /**
     * Long description
     */
    private final String longDescription;

    /**
     * Sample
     */
    private final String sample;

    /**
     * References
     */
    private final List<DSLDocClass> references = new ArrayList<>();
}
