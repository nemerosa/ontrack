package net.nemerosa.ontrack.extension.stale

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.stale.StaleProperty
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.MultiStrings
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class StalePropertyType(
    extensionFeature: StaleExtensionFeature,
) : AbstractPropertyType<StaleProperty>(extensionFeature) {

    override fun getName(): String = "Stale branches"

    override fun getDescription(): String = "Allows to disable or delete stale branches"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: StaleProperty?): Form {
        return create()
            .with(
                Int.of("disablingDuration")
                    .label("Disabling branches after N (days)")
                    .min(0)
                    .help("Number of days of inactivity after a branch is disabled. 0 means that " +
                            "the branch won't ever be disabled automatically.")
                    .value(value?.disablingDuration ?: 0)
            )
            .with(
                Int.of("deletingDuration")
                    .label("Deleting branches after N (days) more")
                    .min(0)
                    .help("Number of days of inactivity after a branch is deleted, after it has been" +
                            "disabled automatically. 0 means that " +
                            "the branch won't ever be deleted automatically.")
                    .value(value?.deletingDuration ?: 0)
            )
            .with(
                MultiStrings.of("promotionsToKeep")
                    .label("Promotions to keep")
                    .help("List of promotion levels which prevent a branch to be disabled or deleted")
                    .value(value?.promotionsToKeep ?: emptyList<Any>())
            )
            .with(
                Text.of(StaleProperty::includes.name)
                    .label("Exclude branches")
                    .help("Regular expression to identify branches which will never be disabled not deleted")
                    .value(value?.includes)
            )
            .with(
                Text.of(StaleProperty::excludes.name)
                    .label("... but")
                    .help("Can define a regular expression for exceptions to the previous rule")
                    .value(value?.excludes)
            )
    }

    override fun fromClient(node: JsonNode): StaleProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): StaleProperty {
        return parse(node, StaleProperty::class.java)
    }

    override fun replaceValue(value: StaleProperty, replacementFunction: Function<String, String>): StaleProperty {
        return value
    }
}