package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.NameValue
import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern

/**
 * Definition of an event
 */
class Event(
    val eventType: EventType,
    val signature: Signature?,
    val entities: Map<ProjectEntityType, ProjectEntity>,
    val extraEntities: Map<ProjectEntityType, ProjectEntity>,
    val ref: ProjectEntityType?,
    val values: Map<String, NameValue>,
) {

    fun getIntValue(name: String): Int {
        return getValue(name).toInt(10)
    }

    fun getValue(name: String): String =
        values[name]?.value ?: error("Missing value '$name' in the event.")

    @Suppress("UNCHECKED_CAST")
    fun <T : ProjectEntity> getEntity(entityType: ProjectEntityType): T =
        entities[entityType] as T ?: error("Missing entity $entityType in the event.")

    @Suppress("UNCHECKED_CAST")
    fun <T : ProjectEntity?> getExtraEntity(entityType: ProjectEntityType): T =
        extraEntities[entityType] as T ?: error("Missing extra entity X_$entityType in the event.")

    fun renderText(): String {
        return render(PlainEventRenderer.INSTANCE)
    }

    fun render(eventRenderer: EventRenderer): String {
        val m = EXPRESSION.matcher(eventType.template)
        val output = StringBuilder()
        while (m.find()) {
            val value = expandExpression(m.group(1), eventRenderer)
            m.appendReplacement(output, value)
        }
        m.appendTail(output)
        return output.toString()
    }

    private fun expandExpression(expression: String, eventRenderer: EventRenderer): String {
        return if (StringUtils.startsWith(expression, ":")) {
            val linkIndex = expression.indexOf(":", 1)
            if (linkIndex > 0) {
                val textKey = expression.substring(1, linkIndex)
                val text = values[textKey] ?: throw EventMissingValueException(eventType.template, textKey)
                val linkKey = expression.substring(linkIndex + 1)
                val link = values[linkKey] ?: throw EventMissingValueException(eventType.template, linkKey)
                eventRenderer.renderLink(text, link, this)
            } else {
                val valueKey = expression.substring(1)
                val value = values[valueKey] ?: throw EventMissingValueException(eventType.template, valueKey)
                eventRenderer.render(valueKey, value, this)
            }
        } else if ("REF" == expression) {
            if (ref == null) {
                throw EventMissingRefEntityException(eventType.template)
            } else {
                val entity = entities[ref] ?: throw EventMissingEntityException(eventType.template, ref)
                eventRenderer.render(entity, this)
            }
        } else if (expression.startsWith("X_")) {
            // Final reference
            val type = expression.substring(2)
            // Project entity type
            val projectEntityType = ProjectEntityType.valueOf(type)
            // Gets the corresponding entity
            val projectEntity = extraEntities[projectEntityType]
                ?: throw EventMissingEntityException(eventType.template, projectEntityType)
            // Rendering
            eventRenderer.render(projectEntity, this)
        } else {
            // Project entity type
            val projectEntityType = ProjectEntityType.valueOf(expression)
            // Gets the corresponding entity
            val projectEntity = entities[projectEntityType]
                ?: throw EventMissingEntityException(eventType.template, projectEntityType)
            // Rendering
            eventRenderer.render(projectEntity, this)
        }
    }

    fun withSignature(signature: Signature?): Event = Event(
        eventType,
        signature,
        entities,
        extraEntities,
        ref,
        values
    )

    class EventBuilder(private val eventType: EventType) {

        private var signature: Signature? = null
        private val entities = mutableMapOf<ProjectEntityType, ProjectEntity>()
        private val extraEntities = mutableMapOf<ProjectEntityType, ProjectEntity>()
        private var ref: ProjectEntityType? = null
        private val values = mutableMapOf<String, NameValue>()

        fun with(signature: Signature?): EventBuilder {
            this.signature = signature
            return this
        }

        fun withNoSignature(): EventBuilder {
            signature = null
            return this
        }

        fun withBuild(build: Build): EventBuilder {
            return withBranch(build.branch).with(build).with(build.signature)
        }

        fun withPromotionRun(promotionRun: PromotionRun): EventBuilder {
            return withBuild(promotionRun.build).with(promotionRun).with(promotionRun.promotionLevel)
                .with(promotionRun.signature)
        }

        fun withValidationRun(validationRun: ValidationRun): EventBuilder {
            return withBuild(validationRun.build).with(validationRun.validationStamp).with(validationRun)
                .with(validationRun.lastStatus.signature)
        }

        fun withPromotionLevel(promotionLevel: PromotionLevel): EventBuilder {
            return withBranch(promotionLevel.branch).with(promotionLevel)
        }

        fun withValidationStamp(validationStamp: ValidationStamp): EventBuilder {
            return withBranch(validationStamp.branch).with(validationStamp)
        }

        fun withBranch(branch: Branch): EventBuilder {
            return withProject(branch.project).with(branch)
        }

        fun withProject(project: Project): EventBuilder {
            return with(project)
        }

        fun withExtraProject(project: Project): EventBuilder {
            return withExtra(project)
        }

        fun withRef(entity: ProjectEntity): EventBuilder {
            ref = entity.projectEntityType
            return withProject(entity.project).with(entity)
        }

        fun with(entity: ProjectEntity): EventBuilder {
            entities[entity.projectEntityType] = entity
            return this
        }

        fun withExtra(entity: ProjectEntity): EventBuilder {
            extraEntities[entity.projectEntityType] = entity
            return this
        }

        fun withValidationRunStatus(statusID: ValidationRunStatusID): EventBuilder {
            return with("status", NameValue(statusID.id, statusID.name))
        }

        fun with(name: String, value: NameValue): EventBuilder {
            values[name] = value
            return this
        }

        fun with(name: String, value: String?): EventBuilder {
            return with(name, NameValue(name, value!!))
        }

        fun get(): Event {
            // Creates the event
            val event = Event(
                eventType,
                signature,
                entities,
                extraEntities,
                ref,
                values
            )
            // Checks the event can be resolved with all its references
            event.renderText()
            // OK
            return event
        }
    }

    companion object {

        private val EXPRESSION = Pattern.compile("\\$\\{([:a-zA-Z_-]+)}")

        @JvmStatic
        fun of(eventType: EventType): EventBuilder {
            return EventBuilder(eventType)
        }

    }
}