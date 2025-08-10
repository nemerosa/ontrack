package net.nemerosa.ontrack.ui.support

import jakarta.servlet.http.HttpServletResponse
import net.nemerosa.ontrack.common.Document
import org.springframework.http.CacheControl
import java.util.concurrent.TimeUnit

object UIUtils {

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
