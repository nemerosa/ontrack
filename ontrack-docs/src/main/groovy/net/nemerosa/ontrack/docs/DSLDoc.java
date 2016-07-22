package net.nemerosa.ontrack.docs;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Generated documentation
 */
@Data
public class DSLDoc {

    /**
     * Indexation of classes
     */
    private final Map<String, DSLDocClass> classes = new TreeMap<>();

}
