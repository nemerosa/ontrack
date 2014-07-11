package net.nemerosa.ontrack.boot.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import net.nemerosa.ontrack.json.JsonUtils;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public final class UIUtils {

    private UIUtils() {
    }

    public static JsonNode requestParametersToJson(WebRequest request) {
        // Gets the parameters
        Map<String, String[]> requestParameters = request.getParameterMap();
        // Converts the request parameters to single values
        Map<String, String> parameters = Maps.transformValues(
                requestParameters,
                array -> {
                    if (array == null || array.length == 0) {
                        return null;
                    } else if (array.length == 1) {
                        return array[0];
                    } else {
                        throw new IllegalArgumentException("Cannot accept several identical parameters");
                    }
                }
        );
        // Gets the parameters as JSON
        return JsonUtils.mapToJson(parameters);
    }
}
