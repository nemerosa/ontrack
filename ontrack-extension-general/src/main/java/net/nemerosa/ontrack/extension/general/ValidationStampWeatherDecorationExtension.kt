package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class ValidationStampWeatherDecorationExtension(
    extensionFeature: GeneralExtensionFeature,
    private val structureService: StructureService
) : AbstractExtension(extensionFeature), DecorationExtension<ValidationStampWeatherDecoration> {

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.VALIDATION_STAMP)
    }

    override fun getDecorations(entity: ProjectEntity): List<Decoration<ValidationStampWeatherDecoration>> {
        // Argument check
        check(entity is ValidationStamp) { "Expecting validation stamp" }
        // List of the last five runs for this validation stamp
        val runs: List<ValidationRun> =
            structureService.getValidationRunsForValidationStamp(entity as ValidationStamp, 0, 5)
        // Keeps only the ones that are not passed
        val notPassed = runs.count { run: ValidationRun -> !run.isPassed }
        // Result
        val decoration: ValidationStampWeatherDecoration = when (notPassed) {
            0 -> ValidationStampWeatherDecoration(
                Weather.sunny,
                "Sunny (0 failure in the last 5 builds)"
            )

            1 -> ValidationStampWeatherDecoration(
                Weather.sunAndClouds,
                "Sun and clouds (1 failure in the last 5 builds)"
            )

            2 -> ValidationStampWeatherDecoration(
                Weather.clouds,
                "Clouds (2 failures in the last 5 builds)"
            )

            3 -> ValidationStampWeatherDecoration(
                Weather.rain,
                "Rain (3 failures in the last 5 builds)"
            )

            else -> ValidationStampWeatherDecoration(
                Weather.storm,
                "Storm (4 failures or more in the last 5 builds)"
            )
        }
        // OK
        return listOf(
            Decoration.of(
                this,
                decoration
            )
        )
    }
}
