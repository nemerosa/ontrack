package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.extension.ExtensionList;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * Getting the list of extensions and their properties.
 */
@RestController
@RequestMapping("/rest/extensions")
public class ExtensionController extends AbstractResourceController {

    private final ExtensionManager extensionManager;

    @Autowired
    public ExtensionController(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    /**
     * Gets the list of extensions.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<ExtensionList> getExtensions() {
        return Resource.of(
                extensionManager.getExtensionList(),
                uri(MvcUriComponentsBuilder.on(getClass()).getExtensions())
        );
    }

}
