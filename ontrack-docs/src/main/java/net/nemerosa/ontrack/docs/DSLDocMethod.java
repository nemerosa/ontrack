package net.nemerosa.ontrack.docs;

import lombok.Data;

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
     * Sample
     */
    private final String sample;
}
