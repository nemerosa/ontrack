package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.exceptions.ImageFileSizeException
import net.nemerosa.ontrack.model.exceptions.ImageTypeNotAcceptedException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object ImageHelper {

    private const val ICON_IMAGE_SIZE_MAX = 16 * 1000L

    private val IMAGE_PNG = "image/png"

    private val ACCEPTED_IMAGE_TYPES = listOf(
        IMAGE_PNG,
    )

    @OptIn(ExperimentalEncodingApi::class)
    fun imagePng(base64: String) = Document(
        type = IMAGE_PNG,
        content = Base64.decode(base64)
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
