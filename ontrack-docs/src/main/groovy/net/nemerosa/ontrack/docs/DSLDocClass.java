package net.nemerosa.ontrack.docs;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
     * Long description
     */
    private final String longDescription;

    /**
     * Groovy sample
     */
    private final String sample;

    /**
     * All methods
     */
    private final List<DSLDocMethod> methods = new ArrayList<>();

    /**
     * Technical ID for the class
     *
     * @return ID used to uniquely identify the class
     */
    public String getId() {
        return name.toLowerCase();
    }

    /**
     * Property class?
     */
    private final boolean propertyClass;

    /**
     * Property class (if any)
     */
    private final AtomicReference<DSLDocClass> properties = new AtomicReference<>();

    /**
     * References
     */
    private final List<DSLDocClass> references = new ArrayList<>();
}
