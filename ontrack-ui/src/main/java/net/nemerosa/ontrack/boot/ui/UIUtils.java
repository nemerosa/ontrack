package net.nemerosa.ontrack.boot.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.json.JsonUtils;
import org.springframework.http.CacheControl;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    public static void setupDefaultImageCache(HttpServletResponse response, Document document) {
        setupImageCache(response, document, 1);
    }

    public static void setupImageCache(HttpServletResponse response, Document document, int maxDays) {
        if (!document.isEmpty()) {
            String cacheControl = CacheControl.maxAge(maxDays, TimeUnit.DAYS).cachePublic().getHeaderValue();
            response.setHeader("Cache-Control", cacheControl);
        }
    }
}
