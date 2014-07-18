package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/permissions")
public class PermissionController extends AbstractResourceController {

    /**
     * TODO List of global permissions
     */
    @RequestMapping(value = "globals", method = RequestMethod.GET)
    public Resources<GlobalPermission> getGlobalPermissions() {
        return null;
    }

    /**
     * TODO List of global roles
     */
    @RequestMapping(value = "globals/roles", method = RequestMethod.GET)
    public Resources<GlobalRole> getGlobalRoles() {
        return null;
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
    public Ack saveGlobalPermission(@PathVariable PermissionTargetType type, @PathVariable int id, @RequestBody PermissionInput input) {
        return Ack.NOK;
    }

    /**
     * TODO Deleting a global permission
     */
    @RequestMapping(value = "globals/{type}/{id}", method = RequestMethod.DELETE)
    public Ack deleteGlobalPermission(@PathVariable PermissionTargetType type, @PathVariable int id) {
        return Ack.NOK;
    }

}
