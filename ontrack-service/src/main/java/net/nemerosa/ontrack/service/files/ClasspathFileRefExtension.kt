package net.nemerosa.ontrack.service.files

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.api.FileRefExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.support.CoreExtensionFeature
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Looking for a file in the classpath
 */
@Component
@Profile(RunProfile.DEV)
class ClasspathFileRefExtension : AbstractExtension(CoreExtensionFeature.INSTANCE), FileRefExtension {

    override val protocol: String = "classpath"

    override fun download(path: String, type: String): Document =
        ClasspathFileRefExtension::class.java.getResourceAsStream(path)
            ?.use {
                it.readBytes()
            }
            ?.let {
                Document(
                    type = type,
                    content = it,
                )
            }
            ?: Document.EMPTY
}