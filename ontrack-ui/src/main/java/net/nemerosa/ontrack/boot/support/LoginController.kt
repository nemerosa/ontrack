package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.UILoginExtension
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController(
        private val extensionManager: ExtensionManager
) {

    @GetMapping("/login")
    fun login(model: Model): String {
        val extensions = extensionManager.getExtensions(UILoginExtension::class.java).flatMap {
            it.contributions
        }
        model.addAttribute("extensions", extensions)
        return "login"
    }

}