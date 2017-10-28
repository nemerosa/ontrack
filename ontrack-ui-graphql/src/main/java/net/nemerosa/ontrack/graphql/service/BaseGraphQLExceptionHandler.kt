package net.nemerosa.ontrack.graphql.service

import net.nemerosa.ontrack.common.BaseException
import org.springframework.stereotype.Component

@Component
class BaseGraphQLExceptionHandler : AbstractGraphQLExceptionHandler(BaseException::class)