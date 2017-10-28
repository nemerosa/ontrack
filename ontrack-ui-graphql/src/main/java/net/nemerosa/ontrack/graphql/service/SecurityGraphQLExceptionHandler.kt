package net.nemerosa.ontrack.graphql.service

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component
class SecurityGraphQLExceptionHandler : AbstractGraphQLExceptionHandler(AccessDeniedException::class)