package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.AccountController;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class AccountGroupResourceDecorator extends AbstractResourceDecorator<AccountGroup> {

    public AccountGroupResourceDecorator() {
        super(AccountGroup.class);
    }

    @Override
    public List<Link> links(AccountGroup group, ResourceContext resourceContext) {
        return resourceContext.links()
                // Self
                .self(on(AccountController.class).getGroup(group.getId()))
                        // Update
                .link(Link.UPDATE, on(AccountController.class).getGroupUpdateForm(group.getId()))
                        // Delete
                .link(Link.DELETE, on(AccountController.class).deleteGroup(group.getId()))
                        // OK
                .build();
    }
}
