package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/accounts/permissions")
public class PermissionController extends AbstractResourceController {

    private final AccountService accountService;
    private final RolesService rolesService;

    @Autowired
    public PermissionController(AccountService accountService, RolesService rolesService) {
        this.accountService = accountService;
        this.rolesService = rolesService;
    }

    /**
     * Looking for a permission target
     */
    @RequestMapping(value = "search/{token:.*}", method = RequestMethod.GET)
    public List<PermissionTarget> searchPermissionTargets(@PathVariable String token) {
        return accountService.searchPermissionTargets(token).stream().toList();
    }

    /**
     * List of global permissions
     */
    @RequestMapping(value = "globals", method = RequestMethod.GET)
    public List<GlobalPermission> getGlobalPermissions() {
        return accountService.getGlobalPermissions().stream().toList();
    }

    /**
     * List of global roles
     */
    @RequestMapping(value = "globals/roles", method = RequestMethod.GET)
    public List<GlobalRole> getGlobalRoles() {
        return rolesService.getGlobalRoles();
    }

    /**
     * Saving a global permission
     */
    @RequestMapping(value = "globals/{type}/{id}", method = RequestMethod.PUT)
    public Ack saveGlobalPermission(@PathVariable PermissionTargetType type, @PathVariable int id, @RequestBody PermissionInput input) {
        return accountService.saveGlobalPermission(type, id, input);
    }

    /**
     * Deleting a global permission
     */
    @RequestMapping(value = "globals/{type}/{id}", method = RequestMethod.DELETE)
    public Ack deleteGlobalPermission(@PathVariable PermissionTargetType type, @PathVariable int id) {
        return accountService.deleteGlobalPermission(type, id);
    }

    /**
     * List of project roles
     */
    @RequestMapping(value = "projects/roles", method = RequestMethod.GET)
    public List<ProjectRole> getProjectRoles() {
        return rolesService.getProjectRoles();
    }

    /**
     * List of permissions for a project.
     */
    @RequestMapping(value = "projects/{projectId}", method = RequestMethod.GET)
    public List<ProjectPermission> getProjectPermissions(@PathVariable ID projectId) {
        return accountService.getProjectPermissions(projectId).stream().toList();
    }

    /**
     * Saving a project permission
     */
    @RequestMapping(value = "projects/{projectId}/{type}/{id}", method = RequestMethod.PUT)
    public Ack saveProjectPermission(@PathVariable ID projectId, @PathVariable PermissionTargetType type, @PathVariable int id, @RequestBody PermissionInput input) {
        return accountService.saveProjectPermission(projectId, type, id, input);
    }

    /**
     * Deleting a project permission
     */
    @RequestMapping(value = "projects/{projectId}/{type}/{id}", method = RequestMethod.DELETE)
    public Ack deleteProjectPermission(@PathVariable ID projectId, @PathVariable PermissionTargetType type, @PathVariable int id) {
        return accountService.deleteProjectPermission(projectId, type, id);
    }

}
