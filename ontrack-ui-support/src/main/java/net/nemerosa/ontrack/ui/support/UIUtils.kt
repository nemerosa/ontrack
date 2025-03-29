package net.nemerosa.ontrack.ui.support

import com.fasterxml.jackson.databind.JsonNode
import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.json.JsonUtils
import org.springframework.http.CacheControl
import org.springframework.web.context.request.WebRequest
import java.util.concurrent.TimeUnit

object UIUtils {

    @JvmStatic
    fun requestParametersToJson(request: WebRequest): JsonNode? {
        // Gets the parameters
        val requestParameters = request.parameterMap
        // Converts the request parameters to single values
        val parameters = requestParameters.mapValues { (_, array) ->
            when {
                array == null || array.isEmpty() -> null
                array.size == 1 -> array[0]
                else -> throw IllegalArgumentException("Cannot accept several identical parameters")
            }
        }
        // Gets the parameters as JSON
        return JsonUtils.mapToJson(parameters)
    }

    @JvmStatic
    fun setupDefaultImageCache(response: HttpServletResponse?, document: Document) {
        setupImageCache(response, document, 1)
    }

    @JvmStatic
    fun setupImageCache(response: HttpServletResponse?, document: Document, maxDays: Int) {
        if (!document.isEmpty) {
            val cacheControl = CacheControl.maxAge(maxDays.toLong(), TimeUnit.DAYS).cachePublic().headerValue
            response?.setHeader("Cache-Control", cacheControl)
        }
    }
}
