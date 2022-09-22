package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.common.UserException
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Transactional(
    noRollbackFor = [
        UserException::class,
    ]
)
annotation class UserTransaction
