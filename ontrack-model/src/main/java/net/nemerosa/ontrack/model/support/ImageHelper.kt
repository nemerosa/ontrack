package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.exceptions.ImageFileSizeException
import net.nemerosa.ontrack.model.exceptions.ImageTypeNotAcceptedException

object ImageHelper {

    private const val ICON_IMAGE_SIZE_MAX = 16 * 1000L

    private val ACCEPTED_IMAGE_TYPES = listOf(
        "image/jpeg",
        "image/png",
        "image/gif"
    )

    @JvmStatic
    fun checkImage(document: Document?) {
        // Checks the image type
        if (document != null && !ACCEPTED_IMAGE_TYPES.contains(document.type)) {
            throw ImageTypeNotAcceptedException(document.type, *ACCEPTED_IMAGE_TYPES.toTypedArray())
        }
        // Checks the image length
        val size = document?.content?.size ?: 0
        if (size > ICON_IMAGE_SIZE_MAX) {
            throw ImageFileSizeException(size.toLong(), ICON_IMAGE_SIZE_MAX)
        }
    }
}
