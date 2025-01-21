package net.nemerosa.ontrack.extension.scm.files

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.FileRefExtension
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.service.SCMExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class SCMFileRefExtension(
    extensionFeature: SCMExtensionFeature,
    private val extensionManager: ExtensionManager,
) : AbstractExtension(extensionFeature), FileRefExtension {

    override val protocol: String = "scm"

    private val scmExtensions: Map<String, SCMExtension> by lazy {
        extensionManager.getExtensions(SCMExtension::class.java)
            .associateBy { it.type }
    }

    override fun download(path: String, type: String): Document? {
        val ref = SCMRef.parseUri(path) ?: throw SCMRefParsingException(path)
        val extension = scmExtensions[ref.type]
            ?: throw SCMRefUnknownSCMTypeException(ref.type)

        val (scm, scmPath) = extension.getSCMPath(ref.config, ref.ref) ?: return null

        val bytes = scm.download(
            scmBranch = null, // Using the default branch
            path = scmPath,
            retryOnNotFound = false,
        ) ?: return null

        return Document(type, bytes)
    }
}