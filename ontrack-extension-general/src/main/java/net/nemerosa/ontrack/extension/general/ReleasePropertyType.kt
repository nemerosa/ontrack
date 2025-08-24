package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.security.PromotionRunCreate
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReleasePropertyType(
    extensionFeature: GeneralExtensionFeature,
    private val searchIndexService: SearchIndexService,
    private val releaseSearchExtension: ReleaseSearchExtension,
    private val releasePropertyListeners: List<ReleasePropertyListener> = emptyList(),
) : AbstractPropertyType<ReleaseProperty>(extensionFeature) {

    override val name: String = "Release"

    override val description: String = "Release indicator on the build."

    override val supportedEntityTypes: Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.BUILD)

    /**
     * If one can promote a build, he can also attach a release label to a build.
     */
    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, PromotionRunCreate::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun onPropertyChanged(entity: ProjectEntity, value: ReleaseProperty) {
        releasePropertyListeners.forEach { listener ->
            listener.onBuildReleaseLabel(entity as Build, value)
        }
        searchIndexService.createSearchIndex(releaseSearchExtension, ReleaseSearchItem(entity, value))
    }

    override fun onPropertyDeleted(entity: ProjectEntity, oldValue: ReleaseProperty) {
        searchIndexService.deleteSearchIndex(releaseSearchExtension, ReleaseSearchItem(entity, oldValue).id)
    }

    override fun fromClient(node: JsonNode): ReleaseProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): ReleaseProperty {
        return ReleaseProperty(
                node.path("name").asText()
        )
    }

    override fun replaceValue(value: ReleaseProperty, replacementFunction: (String) -> String): ReleaseProperty {
        return value
    }

    override fun containsValue(value: ReleaseProperty, propertyValue: String): Boolean =
            if ("*" in propertyValue) {
                val regex = propertyValue.replace("*", ".*").toRegex(RegexOption.IGNORE_CASE)
                regex.matches(value.name)
            } else {
                value.name.equals(propertyValue, ignoreCase = true)
            }

    override fun getSearchArguments(token: String): PropertySearchArguments? {
        return if (token.isNotBlank()) {
            if ("*" in token) {
                PropertySearchArguments(
                        null,
                        "pp.json->>'name' ilike :token",
                        mapOf("token" to token.replace("*", "%"))
                )
            } else {
                PropertySearchArguments(
                        null,
                        "UPPER(pp.json->>'name') = UPPER(:token)",
                        mapOf("token" to token)
                )
            }
        } else {
            null
        }
    }
}