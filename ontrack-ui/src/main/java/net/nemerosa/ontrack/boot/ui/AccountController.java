package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Management of accounts
 */
@RestController
@RequestMapping("/accounts")
public class AccountController extends AbstractResourceController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * List of accounts
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resources<Account> getAccounts() {
        return Resources.of(
                accountService.getAccounts(),
                uri(on(getClass()).getAccounts())
        );
    }

}
