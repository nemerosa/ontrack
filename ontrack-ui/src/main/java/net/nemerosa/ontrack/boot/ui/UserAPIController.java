package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.ConnectedAccount;
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

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/user")
public class UserAPIController extends AbstractResourceController implements UserAPI {

    private final SecurityService securityService;

    @Autowired
    public UserAPIController(SecurityService securityService) {
        this.securityService = securityService;
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
        return Resource.of(
                ConnectedAccount.of(account),
                uri(on(UserAPIController.class).getCurrentUser())
        );
        // TODO Logged user menu
    }
}
