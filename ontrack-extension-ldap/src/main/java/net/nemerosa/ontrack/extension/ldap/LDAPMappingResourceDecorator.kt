package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;

@Component
public class LDAPMappingResourceDecorator extends AbstractResourceDecorator<LDAPMapping> {

    public LDAPMappingResourceDecorator() {
        super(LDAPMapping.class);
    }

    @Override
    public List<Link> links(LDAPMapping resource, ResourceContext resourceContext) {
        return resourceContext.links()
//                // Update
//                .link(Link.UPDATE, MvcUriComponentsBuilder.on(LDAPController.class).getMappingUpdateForm(resource.getId()))
//                        // Delete
//                .link(Link.DELETE, MvcUriComponentsBuilder.on(LDAPController.class).deleteMapping(resource.getId()))
                        // OK
                .build();
    }
}
