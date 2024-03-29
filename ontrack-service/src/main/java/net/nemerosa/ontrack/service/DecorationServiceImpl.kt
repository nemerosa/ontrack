package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DecorationServiceImpl(
    private val extensionManager: ExtensionManager,
    private val securityService: SecurityService,
    private val applicationLogService: ApplicationLogService,
) : DecorationService {

    override fun getDecorations(entity: ProjectEntity): List<Decoration<*>> {
        // Downloading a decoration with the current security context
        val securedDecoratorFunction = securityService.runner { decorator: Decorator<*> -> getDecorations(entity, decorator) }
        // OK
        return extensionManager.getExtensions(DecorationExtension::class.java)
                // ... and filters per entity
                .filter { decorator: DecorationExtension<*> -> decorator.scope.contains(entity.projectEntityType) }
                // ... and gets the decoration
                .flatMap(securedDecoratorFunction)
    }

    /**
     * Gets the decoration for an entity, and returns an "error" decoration in case of problem.
     */
    fun <T> getDecorations(entity: ProjectEntity, decorator: Decorator<T>): List<Decoration<*>> {
        return try {
            decorator.getDecorations(entity)
        } catch (ex: Exception) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    ex,
                    NameDescription.nd("decoration-error", "Decoration error"),
                    "Error while getting decoration ${decorator::class.java.simpleName} on ${entity.entityDisplayName}"
                )
                    .withDetail("entity", entity.entityDisplayName)
                    .withDetail("decorator", decorator::class.java.simpleName)
            )
            listOf(
                    Decoration.error(decorator, getErrorMessage(ex))
            )
        }
    }

    /**
     * Decoration error message
     */
    protected fun getErrorMessage(ex: Exception): String? = if (ex is BaseException) {
        ex.message
    } else {
        "Problem while getting decoration"
    }

}