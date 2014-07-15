package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractResourceController {

    private final SecurityService securityService;
    private final ExtensionManager extensionManager;

    @Autowired
    public UserController(SecurityService securityService, ExtensionManager extensionManager) {
        this.securityService = securityService;
        this.extensionManager = extensionManager;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ConnectedAccount getCurrentUser() {
        // Gets the current account
        Account account = securityService.getCurrentAccount();
        // Account present
        if (account != null) {
            return toLoggedAccount(account);
        }
        // Not logged
        else {
            return toAnonymousAccount();
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public Form loginForm() {
        return Form.create()
                .name()
                .password();
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ConnectedAccount login() {
        // Gets the current account
        Account account = securityService.getCurrentAccount();
        // If not logged, rejects
        if (account == null) {
            throw new AccessDeniedException("Login required.");
        }
        // Already logged
        else {
            return toLoggedAccount(account);
        }
    }

    @RequestMapping(value = "logged-out", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void loggedOut() {
    }

    // Resource assemblers

    private ConnectedAccount toAnonymousAccount() {
        return ConnectedAccount.none();
    }

    private ConnectedAccount toLoggedAccount(Account account) {
        return userMenu(ConnectedAccount.of(account));
    }

    private ConnectedAccount userMenu(ConnectedAccount user) {
        // Settings
        if (securityService.isGlobalFunctionGranted(GlobalSettings.class)) {
            user.add(Action.of("settings", "Settings", "settings"));
        }
        // TODO Profile
        // TODO Account management
        // TODO Extension management
        // Contributions from extensions
        user = userMenuExtensions(user);
        // Admin tools
        if (securityService.isGlobalFunctionGranted(ApplicationManagement.class)) {
            user.add(Action.of("admin-console", "Admin console", "admin-console"));
        }
        // OK
        return user;
    }

    private ConnectedAccount userMenuExtensions(ConnectedAccount user) {
        // Gets the list of user menu extensions
        Collection<UserMenuExtension> extensions = extensionManager.getExtensions(UserMenuExtension.class);
        // For each extension
        for (UserMenuExtension extension : extensions) {
            // Granted?
            Class<? extends GlobalFunction> fn = extension.getGlobalFunction();
            if (fn == null || securityService.isGlobalFunctionGranted(fn)) {
                // Adds the menu entry
                // Prepends the extension ID
                user.add(resolveExtensionAction(extension));
            }
        }
        // OK
        return user;
    }
}
