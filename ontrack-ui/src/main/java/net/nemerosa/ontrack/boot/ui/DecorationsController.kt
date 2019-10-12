package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.concurrent.Callable

@RestController
@RequestMapping("/decorations")
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
    ): Callable<Resources<Decoration<*>>> {
        // Gets the current request attributes
        val attributes = RequestContextHolder.currentRequestAttributes()
        return Callable {
            RequestContextHolder.setRequestAttributes(attributes)
            try {
                Resources.of(
                        decorationService.getDecorations(getEntity(entityType, id)),
                        uri(on(javaClass).getDecorations(entityType, id))
                ).forView(Decoration::class.java)
            } finally {
                RequestContextHolder.resetRequestAttributes()
            }
        }
    }

}
