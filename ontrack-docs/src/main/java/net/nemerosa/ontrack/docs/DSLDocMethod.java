package net.nemerosa.ontrack.docs;

import lombok.Data;

/**
 * Documentation for a method
 */
@Data
public class DSLDocMethod {

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
     * Sample
     */
    private final String sample;

    public String getId() {
        // TODO Transforms xX into x-x
        return name.toLowerCase();
    }
}
