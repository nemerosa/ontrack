package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.form.YesNo;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.support.PasswordChange;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractResourceController {

    private final SecurityService securityService;
    private final UserService userService;
    private final ExtensionManager extensionManager;

    @Autowired
    public UserController(SecurityService securityService, UserService userService, ExtensionManager extensionManager) {
        this.securityService = securityService;
        this.userService = userService;
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
                .password()
                .with(YesNo.of("rememberMe").label("Remember me").value(false))
                ;
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

    @RequestMapping(value = "password", method = RequestMethod.GET)
    public Form getChangePasswordForm() {
        return Form.create()
                .with(
                        Password.of("oldPassword")
                                .label("Old password")
                                .help("You need your old password in order to change it. If you do not remember it, " +
                                        "you'll have to contact an administrator who can change it for you.")
                )
                .with(
                        Password.of("newPassword")
                                .label("New password")
                                .withConfirmation()
                )
                ;
    }

    @RequestMapping(value = "password", method = RequestMethod.POST)
    public Ack changePassword(@RequestBody @Valid PasswordChange input) {
        return userService.changePassword(input);
    }

    // Resource assemblers

    private ConnectedAccount toAnonymousAccount() {
        return ConnectedAccount.none(isAuthenticationRequired());
    }

    private boolean isAuthenticationRequired() {
        return !securityService.getSecuritySettings().isGrantProjectViewToAll();
    }

    private ConnectedAccount toLoggedAccount(Account account) {
        return userMenu(ConnectedAccount.of(isAuthenticationRequired(), account));
    }

    private ConnectedAccount userMenu(ConnectedAccount user) {
        // Settings
        if (securityService.isGlobalFunctionGranted(GlobalSettings.class)) {
            user.add(Action.of("settings", "Settings", "settings"));
        }
        // Changing his password
        if (user.getAccount().getAuthenticationSource().isAllowingPasswordChange()) {
            user.add(
                    Action.form(
                            "user-password",
                            "Change password",
                            uri(on(getClass()).getChangePasswordForm())
                    )
            );
        }
        // Account management
        if (securityService.isGlobalFunctionGranted(AccountManagement.class) || securityService.isGlobalFunctionGranted(AccountGroupManagement.class)) {
            user.add(Action.of("admin-accounts", "Account management", "admin-accounts"));
        }
        // Management of predefined validation stamps and promotion levels
        if (securityService.isGlobalFunctionGranted(GlobalSettings.class)) {
            user.add(Action.of("admin-predefined-validation-stamps", "Predefined validation stamps", "admin-predefined-validation-stamps"));
            user.add(Action.of("admin-predefined-promotion-levels", "Predefined promotion levels", "admin-predefined-promotion-levels"));
        }
        // Contributions from extensions
        user = userMenuExtensions(user);
        // Admin tools
        if (securityService.isGlobalFunctionGranted(ApplicationManagement.class)) {
            user.add(Action.of("admin-health", "System health", "admin-health"));
            user.add(Action.of("admin-extensions", "System extensions", "admin-extensions"));
            user.add(Action.of("admin-jobs", "System jobs", "admin-jobs"));
            user.add(Action.of("admin-log-entries", "Log entries", "admin-log-entries"));
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
