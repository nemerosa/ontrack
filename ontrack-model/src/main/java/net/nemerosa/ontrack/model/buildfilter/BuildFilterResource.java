package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.Branch;

/**
 * @param <T> Type of configuration data for this build filter
 */
@Data
public class BuildFilterResource<T> {

    /**
     * Branch
     */
    @JsonIgnore
    private final Branch branch;
    /**
     * Shared filter?
     */
    private final boolean shared;
    /**
     * Name for this filter
     */
    private final String name;
    /**
     * Type for this filter
     */
    private final String type;
    /**
     * Specific data for this filter
     */
    private final T data;

}
