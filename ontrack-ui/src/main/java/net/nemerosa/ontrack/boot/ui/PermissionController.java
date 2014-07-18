package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/accounts/permissions")
public class PermissionController extends AbstractResourceController {

    private final AccountService accountService;
    private final RolesService rolesService;

    @Autowired
    public PermissionController(AccountService accountService, RolesService rolesService) {
        this.accountService = accountService;
        this.rolesService = rolesService;
    }

    /**
     * TODO List of global permissions
     */
    @RequestMapping(value = "globals", method = RequestMethod.GET)
    public Resources<GlobalPermission> getGlobalPermissions() {
        return Resources.of(
                Collections.<GlobalPermission>emptyList(),
                uri(on(PermissionController.class).getGlobalPermissions())
        ).with("_globalRoles", uri(on(PermissionController.class).getGlobalRoles()));
    }

    /**
     * List of global roles
     */
    @RequestMapping(value = "globals/roles", method = RequestMethod.GET)
    public Resources<GlobalRole> getGlobalRoles() {
        return Resources.of(
                rolesService.getGlobalRoles(),
                uri(on(PermissionController.class).getGlobalRoles())
        );
    }

    /**
     * TODO Looking for a permission target
     */
    @RequestMapping(value = "search/{token:.*}", method = RequestMethod.GET)
    public Resources<PermissionTarget> searchPermissionTarget(@PathVariable String token) {
        return null;
    }

    /**
     * TODO Saving a global permission
     */
    @RequestMapping(value = "globals/{type}/{id}", method = RequestMethod.PUT)
    public Ack saveGlobalPermission(@PathVariable PermissionTargetType type, @PathVariable String id, @RequestBody PermissionInput input) {
        return Ack.NOK;
    }

    /**
     * TODO Deleting a global permission
     */
    @RequestMapping(value = "globals/{type}/{id}", method = RequestMethod.DELETE)
    public Ack deleteGlobalPermission(@PathVariable PermissionTargetType type, @PathVariable String id) {
        return Ack.NOK;
    }

}
