package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class StaleTestSupport(
    private val propertyService: PropertyService,
) {

    fun staleBranches(
        project: Project,
        disabling: Int = 0,
        deleting: Int? = null,
        promotionsToKeep: List<String>? = null,
        includes: String? = null,
        excludes: String? = null,
    ) {
        propertyService.editProperty(
            project,
            StalePropertyType::class.java,
            StaleProperty(
                disablingDuration = disabling,
                deletingDuration = deleting,
                promotionsToKeep = promotionsToKeep,
                includes = includes,
                excludes = excludes,
            )
        )
    }
}