package net.nemerosa.ontrack.service.files

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.FileRefExtension
import net.nemerosa.ontrack.model.files.FileRef
import net.nemerosa.ontrack.model.files.FileRefService
import org.springframework.stereotype.Service

@Service
class FileRefServiceImpl(
    private val extensionManager: ExtensionManager,
) : FileRefService {

    private val extensions: Map<String, FileRefExtension> by lazy {
        extensionManager.getExtensions(FileRefExtension::class.java).associateBy { it.protocol }
    }

    override fun downloadDocument(ref: FileRef, type: String): Document? {
        // Getting the extension
        val extension = extensions[ref.protocol]
            ?: throw FileRefUnsupportedProtocolException(ref.protocol)
        // Using the extension
        return extension.download(ref.path, type)
    }

}