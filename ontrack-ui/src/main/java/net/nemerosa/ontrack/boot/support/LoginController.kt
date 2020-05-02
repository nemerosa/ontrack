package net.nemerosa.ontrack.boot.support

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController(
        private val clientRegistrationRepository: OntrackClientRegistrationRepository
) {

    @GetMapping("/login")
    fun login(model: Model): String {
        val registrations = clientRegistrationRepository.toList()
        model.addAttribute("registrations", registrations)
        return "login"
    }

}