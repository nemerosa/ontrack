package net.nemerosa.ontrack.graphql.support

import net.nemerosa.ontrack.graphql.schema.UserError
import net.nemerosa.ontrack.model.exceptions.InputException
import javax.validation.ConstraintViolation

class MutationInputValidationException(
        val violations: Set<ConstraintViolation<*>>
) : InputException(
        asString(violations)
) {
    companion object {
        fun asString(violations: Set<ConstraintViolation<*>?>): String =
                violations.filterNotNull().joinToString(", ") { cv ->
                    "${cv.propertyPath}: ${cv.message}"
                }

        fun asUserError(cv: ConstraintViolation<*>) = UserError(
                message = cv.message,
                exception = MutationInputValidationException::class.java.name,
                location = cv.propertyPath.toString()
        )
    }
}
