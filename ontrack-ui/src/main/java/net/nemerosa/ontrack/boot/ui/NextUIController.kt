package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.boot.support.NextUIRedirector
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Controller in charge of redirecting the user to the Next UI app.
 */
@Controller
class NextUIController(
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val nextUIRedirector: NextUIRedirector,
) {

    @GetMapping("/rest/ui")
    fun redirectToNextUI(): String {
        val base = ontrackConfigProperties.ui.uri
        val auth = "${base}/auth"
        val uri = nextUIRedirector.redirectURI(
            tokenCallback = auth,
            href = base,
        )
        return "redirect:${uri}"
    }

}