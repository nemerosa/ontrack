package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups;
import net.nemerosa.ontrack.extension.api.UserMenuListExtension;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Password;
import net.nemerosa.ontrack.model.labels.LabelManagement;
import net.nemerosa.ontrack.model.preferences.Preferences;
import net.nemerosa.ontrack.model.preferences.PreferencesService;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.support.PasswordChange;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/rest/user")
public class UserController extends AbstractResourceController {

    private final SecurityService securityService;
    private final UserService userService;
    private final ExtensionManager extensionManager;
    private final PreferencesService preferencesService;

    @Autowired
    public UserController(SecurityService securityService, UserService userService, ExtensionManager extensionManager, PreferencesService preferencesService) {
        this.securityService = securityService;
        this.userService = userService;
        this.extensionManager = extensionManager;
        this.preferencesService = preferencesService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ConnectedAccount getCurrentUser() {
        // Gets the current account
        OntrackAuthenticatedUser user = securityService.getCurrentAccount();
        // Account present
        if (user != null) {
            Preferences preferences = preferencesService.getPreferences(user.getAccount());
            return userMenu(ConnectedAccount.of(user.getAccount(), preferences));
        }
        // Not logged
        else {
            return ConnectedAccount.none();
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

    private ConnectedAccount userMenu(ConnectedAccount user) {
        // Settings
        if (securityService.isGlobalFunctionGranted(GlobalSettings.class)) {
            user.add(
                    Action.of("settings", "Settings", "settings")
                            .withGroup(UserMenuExtensionGroups.system)
            );
        }
        // Access to the user profile
        if (securityService.isLogged()) {
            user.add(
                    Action.of(
                            "user-profile",
                            "User profile",
                            "user-profile"
                    )
            );
        }
        // Account management
        if (securityService.isGlobalFunctionGranted(AccountManagement.class) || securityService.isGlobalFunctionGranted(AccountGroupManagement.class)) {
            user.add(
                    Action.of("admin-accounts", "Account management", "admin-accounts")
                            .withGroup(UserMenuExtensionGroups.security)
            );
        }
        // Management of predefined validation stamps and promotion levels
        if (securityService.isGlobalFunctionGranted(GlobalSettings.class)) {
            user.add(
                    Action.of("admin-predefined-validation-stamps", "Predefined validation stamps", "admin-predefined-validation-stamps")
                            .withGroup(UserMenuExtensionGroups.configuration)
            );
            user.add(
                    Action.of("admin-predefined-promotion-levels", "Predefined promotion levels", "admin-predefined-promotion-levels")
                            .withGroup(UserMenuExtensionGroups.configuration)
            );
        }
        // Management of labels
        if (securityService.isGlobalFunctionGranted(LabelManagement.class)) {
            user.add(
                    Action.of("admin-labels", "Labels", "admin-labels")
                            .withGroup(UserMenuExtensionGroups.configuration)
            );
        }
        // Contributions from extensions
        ConnectedAccount contributed = userMenuExtensions(user);
        // Admin tools
        if (securityService.isGlobalFunctionGranted(ApplicationManagement.class)) {
            contributed.add(
                    Action.of("admin-health", "System health", "admin-health")
                            .withGroup(UserMenuExtensionGroups.system)
            );
            contributed.add(
                    Action.of("admin-extensions", "System extensions", "admin-extensions")
                            .withGroup(UserMenuExtensionGroups.system)
            );
            contributed.add(
                    Action.of("admin-jobs", "System jobs", "admin-jobs")
                            .withGroup(UserMenuExtensionGroups.system)
            );
            contributed.add(
                    Action.of("admin-log-entries", "Log entries", "admin-log-entries")
                            .withGroup(UserMenuExtensionGroups.system)
            );
        }
        // OK
        return contributed;
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
        // Gets the list of user menu list extensions
        Collection<UserMenuListExtension> extensionLists = extensionManager.getExtensions(UserMenuListExtension.class);
        for (UserMenuListExtension extensionList: extensionLists) {
            for (UserMenuExtension extension : extensionList.getUserMenuExtensions()) {
                // Granted?
                Class<? extends GlobalFunction> fn = extension.getGlobalFunction();
                if (fn == null || securityService.isGlobalFunctionGranted(fn)) {
                    // Adds the menu entry
                    // Does NOT prepend the extension ID
                    user.add(extension.getAction());
                }
            }
        }
        // OK
        return user;
    }
}
