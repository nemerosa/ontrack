package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import java.util.concurrent.Callable

@RestController
@RequestMapping("/rest/decorations")
class DecorationsController(
    structureService: StructureService,
    private val decorationService: DecorationService
) : AbstractProjectEntityController(structureService) {

    /**
     * Decorations for an entity.
     */
    @GetMapping("{entityType}/{id}")
    fun getDecorations(
        @PathVariable entityType: ProjectEntityType,
        @PathVariable id: ID
    ): Callable<List<Decoration<*>>> {
        // Gets the current request attributes
        val attributes = RequestContextHolder.currentRequestAttributes()
        return Callable {
            RequestContextHolder.setRequestAttributes(attributes)
            try {
                decorationService.getDecorations(getEntity(entityType, id))
            } finally {
                RequestContextHolder.resetRequestAttributes()
            }
        }
    }

}
