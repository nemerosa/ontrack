package net.nemerosa.ontrack.docs

import org.springframework.stereotype.Component
import java.io.File

/**
 * Support for the documentation generation.
 */
@Component
class DocGenSupport {

    fun inDirectory(path: String, code: DocGenDirectoryContext.() -> Unit) {
        val root = File(DOCS_ROOT)
        val dir = root.resolve(path)
        if (!dir.exists()) {
            dir.mkdirs()
        } else {
            dir.listFiles()?.forEach { file ->
                file.delete()
            }
        }
        val context = DocGenDirectoryContext(dir)
        context.code()
    }

    companion object {
        const val DOCS_ROOT = "src/docs/asciidoc"
    }

}