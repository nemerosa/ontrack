package net.nemerosa.ontrack.model.events

/**
 * This service is used to get the list of template parameters linked to a given event.
 */
interface EventVariableService {

    /**
     * Gets a map which can be used for a template.
     *
     * @param event Event for which to get the parameters
     * @param caseVariants If true, will provide case variants for the same parameter. For example,
     * for a project name like "Ontrack", we'd get: PROJECT=ONTRACK, Project=Ontrack (unchanged), project=ontrack.
     * By default, would return on PROJECT=Ontrack
     */
    @Deprecated("Will be removed in V5. Use the new templating service instead.")
    fun getTemplateParameters(
        event: Event,
        caseVariants: Boolean = false,
    ): Map<String, String>

    /**
     * Gets a map containing all the entities and values for this event.
     *
     * @param event Event for which to get the parameters
     * @param context Initial context
     */
    fun getTemplateContext(event: Event, context: Map<String, Any>): Map<String, Any>

}