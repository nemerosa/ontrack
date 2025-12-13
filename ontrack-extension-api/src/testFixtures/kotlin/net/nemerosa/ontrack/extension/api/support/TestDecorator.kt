package net.nemerosa.ontrack.extension.api.support

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import java.util.*

/**
 * [net.nemerosa.ontrack.model.structure.Decorator] which can be used for tests.
 */
@Component
class TestDecorator(
    extensionFeature: TestExtensionFeature,
    private val propertyService: PropertyService
) : AbstractExtension(extensionFeature), DecorationExtension<TestDecorationData?> {

    override fun getDecorations(entity: ProjectEntity): List<Decoration<TestDecorationData?>>? {
        return propertyService.getProperty(entity, TestDecoratorPropertyType::class.java)
            .value
            ?.let { data ->
                listOf(
                    Decoration.of(
                        this@TestDecorator,
                        data
                    )
                )
            }
            ?: emptyList()
    }

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.allOf(ProjectEntityType::class.java)
}
