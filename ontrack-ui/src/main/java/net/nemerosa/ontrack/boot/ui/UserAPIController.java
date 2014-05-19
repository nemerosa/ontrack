package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    public Resource<Optional<Account>> getCurrentUser() {
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
    public Resource<Optional<Account>> login() {
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

    // Resource assemblers

    private Resource<Optional<Account>> toAnonymousResource() {
        return Resource.of(
                Optional.<Account>ofNullable(null),
                uri(on(UserAPIController.class).getCurrentUser())
        )
                .with("login", uri(on(UserAPIController.class).login()))
                ;
    }

    private Resource<Optional<Account>> toLoggedAccountResource(Account account) {
        return Resource.of(
                Optional.ofNullable(account),
                uri(on(UserAPIController.class).getCurrentUser())
        );
        // TODO Logged user menu
    }
}
