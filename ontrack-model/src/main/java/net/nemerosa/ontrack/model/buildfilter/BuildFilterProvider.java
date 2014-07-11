package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

public interface BuildFilterProvider<T> {

    /**
     * Display name
     */
    String getName();

    /**
     * Gets the form for a new filter on the given branch
     */
    BuildFilterForm newFilterForm(ID branchId);

    BuildFilterForm getFilterForm(ID branchId, T data);

    /**
     * Builds an actual filter using the given set of parameters
     */
    BuildFilter filter(ID branchId, T data);

    Optional<T> parse(JsonNode data);

    @Deprecated
    public static String getParameter(Map<String, String> params, String name) {
        return params.get(name);
    }

    @Deprecated
    public static int getIntParameter(Map<String, String> params, String name, int defaultValue) {
        String value = getParameter(params, name);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            return Integer.parseInt(value, 10);
        } else {
            return defaultValue;
        }
    }

}
