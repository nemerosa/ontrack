package net.nemerosa.ontrack.model.events

/**
 * This service is used to get the list of template parameters linked to a given event.
 */
interface EventVariableService {

    /**
     * Gets a map containing all the entities and values for this event.
     *
     * @param event Event for which to get the parameters
     * @param context Initial context
     */
    fun getTemplateContext(event: Event, context: Map<String, Any>): Map<String, Any>

}