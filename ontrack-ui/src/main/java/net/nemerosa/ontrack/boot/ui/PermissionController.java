package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
    public Resources<PermissionTarget> searchPermissionTargets(@PathVariable String token) {
        return Resources.of(
                accountService.searchPermissionTargets(token),
                uri(on(PermissionController.class).searchPermissionTargets(token))
        );
    }

    /**
     * List of global permissions
     */
    @RequestMapping(value = "globals", method = RequestMethod.GET)
    public Resources<GlobalPermission> getGlobalPermissions() {
        return Resources.of(
                accountService.getGlobalPermissions(),
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
    public Resources<ProjectRole> getProjectRoles() {
        return Resources.of(
                rolesService.getProjectRoles(),
                uri(on(PermissionController.class).getProjectRoles())
        );
    }

    /**
     * List of permissions for a project.
     */
    @RequestMapping(value = "projects/{projectId}", method = RequestMethod.GET)
    public Resources<ProjectPermission> getProjectPermissions(@PathVariable ID projectId) {
        return Resources.of(
                accountService.getProjectPermissions(projectId),
                uri(on(PermissionController.class).getProjectPermissions(projectId))
        ).with("_projectRoles", uri(on(PermissionController.class).getProjectRoles()));
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
