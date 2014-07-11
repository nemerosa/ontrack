package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;

/**
 * @param <T> Type of configuration data for this build filter
 */
@Data
public class BuildFilterResource<T> {

    /**
     * Name for this filter
     */
    private final String name;
    /**
     * Specific data for this filter
     */
    private final T data;

}
