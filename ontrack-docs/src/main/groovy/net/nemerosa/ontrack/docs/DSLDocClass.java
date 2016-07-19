package net.nemerosa.ontrack.docs;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Documentation for a class
 */
@Data
public class DSLDocClass {

    /**
     * Name (simple)
     */
    private final String name;

    /**
     * Description
     */
    private final String description;

    /**
     * Groovy sample
     */
    private final String sample;

    /**
     * All methods
     */
    private final List<DSLDocMethod> methods = new ArrayList<>();

    public String getId() {
        // TODO Transforms xX into x-x
        return name.toLowerCase();
    }

    /**
     * References
     */
    private final List<DSLDocClass> references = new ArrayList<>();
}
