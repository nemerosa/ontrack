package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component
class TestHookEndpointExtension(
    extension: TestExtensionFeature,
    private val testHookInfoLinkExtension: TestHookInfoLinkExtension,
) : AbstractExtension(extension), HookEndpointExtension {

    override var enabled: Boolean = true
    var denied: Boolean = false
    var error: Boolean = false

    override val id: String = "test"

    override fun checkAccess(request: HookRequest) {
        if (denied) {
            throw AccessDeniedException("Hook refused the connection.")
        }
    }

    override fun process(recordId: String, request: HookRequest): HookResponse {
        if (error) {
            throw RuntimeException("Error during the processing.")
        } else {
            return HookResponse(
                    type = HookResponseType.PROCESSED,
                    info = null,
                    infoLink = testHookInfoLinkExtension.createHookInfoLink(
                            "Processing: ${request.body}"
                    ),
            )
        }
    }

}