package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.extension.ExtensionList
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Getting the list of extensions and their properties.
 */
@RestController
@RequestMapping("/rest/extensions")
class ExtensionController(
    private val extensionManager: ExtensionManager
) : AbstractResourceController() {

    @GetMapping("")
    fun getExtensions(): ExtensionList = extensionManager.extensionList
}