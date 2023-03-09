package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.extension.ExtensionList
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

/**
 * Getting the list of extensions and their properties.
 */
@RestController
@RequestMapping("/rest/extensions")
class ExtensionController(
    private val extensionManager: ExtensionManager
) : AbstractResourceController() {

    @GetMapping("")
    fun getExtensions(): Resource<ExtensionList> = Resource.of(
        extensionManager.extensionList,
        uri(MvcUriComponentsBuilder.on(javaClass).getExtensions())
    )
}