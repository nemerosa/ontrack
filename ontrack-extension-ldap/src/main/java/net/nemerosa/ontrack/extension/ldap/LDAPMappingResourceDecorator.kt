package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.linkTo
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class LDAPMappingResourceDecorator : AbstractLinkResourceDecorator<LDAPMapping>(LDAPMapping::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<LDAPMapping>> = listOf(
            Link.UPDATE linkTo { mapping -> on(LDAPController::class.java).getMappingUpdateForm(mapping.id) },
            Link.DELETE linkTo { mapping -> on(LDAPController::class.java).deleteMapping(mapping.id) }
    )

}