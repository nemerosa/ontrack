package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.api.ExtensionManager
import org.springframework.stereotype.Service

@Service
class SCMRefServiceImpl(
    private val extensionManager: ExtensionManager,
) : SCMRefService {

    override fun downloadDocument(ref: SCMRef, type: String): Document? {
        check(ref.protocol == SCMRef.PROTOCOL) {
            "Only the scm protocol is supported for now."
        }

        val extension = extensionManager.getExtensions(SCMExtension::class.java).find {
            it.type == ref.type
        } ?: throw SCMRefUnknownSCMTypeException(ref.type)

        val (scm, path) = extension.getSCMPath(ref.config, ref.ref) ?: return null

        val bytes = scm.download(
            scmBranch = null, // Using the default branch
            path = path,
            retryOnNotFound = false,
        ) ?: return null

        return Document(type, bytes)
    }

}