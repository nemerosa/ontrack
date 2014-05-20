package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.UserMenuExtension;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.ConnectedAccount;
import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/user")
public class UserAPIController extends AbstractResourceController implements UserAPI {

    private final SecurityService securityService;
    private final ExtensionManager extensionManager;

    @Autowired
    public UserAPIController(SecurityService securityService, ExtensionManager extensionManager) {
        this.securityService = securityService;
        this.extensionManager = extensionManager;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<ConnectedAccount> getCurrentUser() {
        // Gets the current account
        Account account = securityService.getCurrentAccount();
        // Account present
        if (account != null) {
            return toLoggedAccountResource(account);
        }
        // Not logged
        else {
            return toAnonymousResource();
        }
    }

    @Override
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public Form loginForm() {
        return Form.create()
                .name()
                .password();
    }

    @Override
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Resource<ConnectedAccount> login() {
        // Gets the current account
        Account account = securityService.getCurrentAccount();
        // If not logged, rejects
        if (account == null) {
            throw new AccessDeniedException("Login required.");
        }
        // Already logged
        else {
            return toLoggedAccountResource(account);
        }
    }

    @RequestMapping(value = "logged-out", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void loggedOut() {
    }

    // Resource assemblers

    private Resource<ConnectedAccount> toAnonymousResource() {
        return Resource.of(
                ConnectedAccount.none(),
                uri(on(UserAPIController.class).getCurrentUser())
        )
                .with("login", uri(on(UserAPIController.class).loginForm()))
                ;
    }

    private Resource<ConnectedAccount> toLoggedAccountResource(Account account) {
        Resource<ConnectedAccount> user = Resource.of(
                ConnectedAccount.of(account),
                uri(on(UserAPIController.class).getCurrentUser())
        );
        return userMenu(user);
    }

    private Resource<ConnectedAccount> userMenu(Resource<ConnectedAccount> user) {
        // TODO Settings
        // TODO Profile
        // TODO Account management
        // TODO Extension management
        // TODO Contributions from extensions
        user = userMenuExtensions(user);
        // OK
        return user;
    }

    private Resource<ConnectedAccount> userMenuExtensions(Resource<ConnectedAccount> user) {
        // Gets the list of user menu extensions
        Collection<UserMenuExtension> extensions = extensionManager.getExtensions(UserMenuExtension.class);
        // For each extension
        for (UserMenuExtension extension : extensions) {
            // Granted?
            Class<? extends GlobalFunction> fn = extension.getGlobalFunction();
            if (fn == null || securityService.isGlobalFunctionGranted(fn)) {
                // FIXME Adds the menu entry
            }
        }
        // OK
        return user;
    }
}
