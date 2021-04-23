package net.nemerosa.ontrack.extension.casc.ui

import net.nemerosa.ontrack.extension.casc.CascLoadingService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/extension/casc")
class CascController(
    private val cascLoadingService: CascLoadingService,
) {

    /**
     * Relods the configuration
     */
    @PutMapping("reload")
    fun reload() {
        cascLoadingService.load()
    }

}