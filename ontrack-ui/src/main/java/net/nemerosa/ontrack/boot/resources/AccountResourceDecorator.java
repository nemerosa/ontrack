package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.AccountController;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class AccountResourceDecorator extends AbstractResourceDecorator<Account> {

    public AccountResourceDecorator() {
        super(Account.class);
    }

    @Override
    public List<Link> links(Account account, ResourceContext resourceContext) {
        return resourceContext.links()
                // Self
                .self(on(AccountController.class).getAccount(account.getId()))
                        // Update
                .link(Link.UPDATE, on(AccountController.class).getUpdateForm(account.getId()))
                        // Delete
                .link(Link.DELETE, on(AccountController.class).deleteAccount(account.getId()), !account.isDefaultAdmin())
                        // OK
                .build();
    }
}
