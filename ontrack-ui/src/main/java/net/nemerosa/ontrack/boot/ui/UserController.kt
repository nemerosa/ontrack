package net.nemerosa.ontrack.boot.ui

import jakarta.validation.Valid
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.labels.LabelManagement
import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.security.ConnectedAccount.Companion.none
import net.nemerosa.ontrack.model.support.Action.Companion.of
import net.nemerosa.ontrack.model.support.PasswordChange
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/user")
class UserController(
    private val securityService: SecurityService,
    private val userService: UserService,
    private val extensionManager: ExtensionManager,
    private val preferencesService: PreferencesService,
) : AbstractResourceController() {

    @GetMapping("")
    fun getCurrentUser(): ConnectedAccount {
        // Gets the current account
        val user = securityService.currentAccount
        // Account present
        return if (user != null) {
            val preferences = preferencesService.getPreferences(user.account)
            userMenu(
                ConnectedAccount(
                    account = user.account,
                    preferences = preferences,
                )
            )
        } else {
            none()
        }
    }

    @GetMapping("logged-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun loggedOut() {
    }

    @GetMapping("password")
    fun getChangePasswordForm(): Form = create()
        .with(
            Password.of("oldPassword")
                .label("Old password")
                .help(
                    "You need your old password in order to change it. If you do not remember it, " +
                            "you'll have to contact an administrator who can change it for you."
                )
        )
        .with(
            Password.of("newPassword")
                .label("New password")
                .withConfirmation()
        )

    @PostMapping("password")
    fun changePassword(@RequestBody input: @Valid PasswordChange?): Ack {
        return userService.changePassword(input!!)
    }

    // Resource assemblers
    private fun userMenu(user: ConnectedAccount): ConnectedAccount {
        // Settings
        if (securityService.isGlobalFunctionGranted(GlobalSettings::class.java)) {
            user.add(
                of("settings", "Settings", "settings")
                    .withGroup(UserMenuExtensionGroups.system)
            )
        }
        // Access to the user profile
        if (securityService.isLogged) {
            user.add(
                of(
                    "user-profile",
                    "User profile",
                    "user-profile"
                )
            )
        }
        // Account management
        if (securityService.isGlobalFunctionGranted(AccountManagement::class.java) || securityService.isGlobalFunctionGranted(
                AccountGroupManagement::class.java
            )
        ) {
            user.add(
                of("admin-accounts", "Account management", "admin-accounts")
                    .withGroup(UserMenuExtensionGroups.security)
            )
        }
        // Management of predefined validation stamps and promotion levels
        if (securityService.isGlobalFunctionGranted(GlobalSettings::class.java)) {
            user.add(
                of(
                    "admin-predefined-validation-stamps",
                    "Predefined validation stamps",
                    "admin-predefined-validation-stamps"
                )
                    .withGroup(UserMenuExtensionGroups.configuration)
            )
            user.add(
                of(
                    "admin-predefined-promotion-levels",
                    "Predefined promotion levels",
                    "admin-predefined-promotion-levels"
                )
                    .withGroup(UserMenuExtensionGroups.configuration)
            )
        }
        // Management of labels
        if (securityService.isGlobalFunctionGranted(LabelManagement::class.java)) {
            user.add(
                of("admin-labels", "Labels", "admin-labels")
                    .withGroup(UserMenuExtensionGroups.configuration)
            )
        }
        // Contributions from extensions
        val contributed = userMenuExtensions(user)
        // Admin tools
        if (securityService.isGlobalFunctionGranted(ApplicationManagement::class.java)) {
            contributed.add(
                of("admin-health", "System health", "admin-health")
                    .withGroup(UserMenuExtensionGroups.system)
            )
            contributed.add(
                of("admin-extensions", "System extensions", "admin-extensions")
                    .withGroup(UserMenuExtensionGroups.system)
            )
            contributed.add(
                of("admin-jobs", "System jobs", "admin-jobs")
                    .withGroup(UserMenuExtensionGroups.system)
            )
            contributed.add(
                of("admin-log-entries", "Log entries", "admin-log-entries")
                    .withGroup(UserMenuExtensionGroups.system)
            )
        }
        // Filtering the user actions
        return contributed.filterActions()
    }

    private fun userMenuExtensions(user: ConnectedAccount): ConnectedAccount {
        // Gets the list of user menu extensions
        val extensions = extensionManager.getExtensions(
            UserMenuExtension::class.java
        )
        // For each extension
        for (extension in extensions) {
            // Granted?
            val fn = extension.globalFunction
            if (fn == null || securityService.isGlobalFunctionGranted(fn)) {
                // Adds the menu entry
                // Prepends the extension ID
                user.add(resolveExtensionAction(extension))
            }
        }
        // OK
        return user
    }
}
