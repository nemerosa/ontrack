package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.PromotionRunCreate
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import net.nemerosa.ontrack.model.structure.SearchIndexService
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class ReleasePropertyType(
        extensionFeature: GeneralExtensionFeature,
        private val searchIndexService: SearchIndexService,
        private val releaseSearchExtension: ReleaseSearchExtension
) : AbstractPropertyType<ReleaseProperty>(extensionFeature) {

    override fun getName(): String = "Release"

    override fun getDescription(): String = "Release indicator on the build."

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.BUILD)

    /**
     * If one can promote a build, he can also attach a release label to a build.
     */
    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, PromotionRunCreate::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun onPropertyChanged(entity: ProjectEntity, value: ReleaseProperty) {
        searchIndexService.createSearchIndex(releaseSearchExtension, ReleaseSearchItem(entity, value))
    }

    override fun onPropertyDeleted(entity: ProjectEntity, oldValue: ReleaseProperty) {
        searchIndexService.deleteSearchIndex(releaseSearchExtension, ReleaseSearchItem(entity, oldValue).id)
    }

    override fun getEditionForm(entity: ProjectEntity, value: ReleaseProperty?): Form {
        return Form.create()
                .with(
                        Text.of("name")
                                .label("Release name")
                                .length(20)
                                .value(value?.name)
                )
    }

    override fun fromClient(node: JsonNode): ReleaseProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): ReleaseProperty {
        return ReleaseProperty(
                node.path("name").asText()
        )
    }

    override fun getSearchKey(value: ReleaseProperty): String {
        return value.name
    }

    override fun replaceValue(value: ReleaseProperty, replacementFunction: Function<String, String>): ReleaseProperty {
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
        return if (!token.isBlank()) {
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