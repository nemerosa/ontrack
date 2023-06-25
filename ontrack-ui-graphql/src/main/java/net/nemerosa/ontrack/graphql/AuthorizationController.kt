package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationService
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class AuthorizationController(
    private val authorizationService: AuthorizationService,
) {

    @QueryMapping
    fun authorizations(): List<Authorization> = authorizationService.authorizations

}