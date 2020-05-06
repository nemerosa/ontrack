package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.UILogin
import net.nemerosa.ontrack.extension.api.UILoginExtension
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LoginController(
        private val extensionManager: ExtensionManager
) {

    @GetMapping("/login")
    fun login(model: Model): String {
        val extensions = loginExtensions
        model.addAttribute("extensions", extensions)
        model.addAttribute("extensionsEnabled", extensions.isNotEmpty())
        return "login"
    }

    /**
     * Getting an image associated with a [login extension][UILoginExtension].
     *
     * @param id [ID][UILogin.id] of the extension
     */
    @GetMapping("/login/extension/{id}/image")
    @ResponseBody
    fun loginExtensionImage(@PathVariable id: String): Document {
        val extensions = loginExtensions
        val extension = extensions.find { it.id == id }
        return if (extension != null && extension.image) {
            extension.imageLoader() ?: Document.EMPTY
        } else {
            Document.EMPTY
        }
    }

    private val loginExtensions: List<UILogin>
        get() = extensionManager.getExtensions(UILoginExtension::class.java).flatMap {
            it.contributions
        }

}