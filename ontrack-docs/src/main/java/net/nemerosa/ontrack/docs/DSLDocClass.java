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
     * All methods
     */
    private final List<DSLDocMethod> methods = new ArrayList<>();

}
