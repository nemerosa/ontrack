package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
            return Resource.of(
                    Optional.ofNullable(account),
                    uri(on(UserAPIController.class).getCurrentUser())
            );
            // TODO Logged user menu
        }
        // Not logged
        else {
            return Resource.of(
                    Optional.ofNullable(account),
                    uri(on(UserAPIController.class).getCurrentUser())
                    // TODO Anonymous user menu?
            );
        }
    }
}
