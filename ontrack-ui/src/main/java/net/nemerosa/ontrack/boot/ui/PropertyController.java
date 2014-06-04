package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UI end point for the management of properties.
 */
@RestController
@RequestMapping("/properties")
public class PropertyController extends AbstractResourceController {
}
