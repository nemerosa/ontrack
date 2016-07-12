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

    // TODO Reference for return and parameters

    /**
     * Description
     */
    private final String description;

    /**
     * Sample
     */
    private final String sample;

}
