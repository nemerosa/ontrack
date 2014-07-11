package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ID;

/**
 * @param <T> Type of configuration data for this build filter
 */
@Data
public class BuildFilterResource<T> {

    /**
     * Branch
     */
    private final ID branchId;
    /**
     * Name for this filter
     */
    private final String name;
    /**
     * Specific data for this filter
     */
    private final T data;

}
