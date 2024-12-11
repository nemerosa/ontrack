package net.nemerosa.ontrack.service.files

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.api.FileRefExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.support.CoreExtensionFeature
import org.springframework.stereotype.Component
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * The path is the Base64 encoding of the file content
 */
@Component
class Base64FileRefExtension : AbstractExtension(CoreExtensionFeature.INSTANCE), FileRefExtension {

    override val protocol: String = "base64"

    @OptIn(ExperimentalEncodingApi::class)
    override fun download(path: String, type: String): Document =
        Document(
            type = type,
            content = Base64.decode(path),
        )
}