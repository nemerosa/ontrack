package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue

interface EventRenderer {

    /**
     * ID of this renderer
     */
    val id: String

    /**
     * Display name for this renderer
     */
    val name: String

    @Deprecated("Will be removed in V5. Use render without event.")
    fun render(projectEntity: ProjectEntity, event: Event): String = render(projectEntity)

    /**
     * Rendering the link to an entity.
     */
    fun render(projectEntity: ProjectEntity): String

    /**
     * Renders a value and gives it some emphasis.
     */
    fun renderStrong(value: String): String

    @Deprecated("Will be removed in V5. Render text directly without the renderer.")
    fun render(valueKey: String, value: NameValue, event: Event): String = renderStrong(value.value)

    @Deprecated("Will be removed in V5. Use renderLink witn strings and without event.")
    fun renderLink(text: NameValue, link: NameValue, event: Event): String =
        renderLink(text.value, link.value)

    /**
     * Renders a link.
     *
     * @param text Text for the link
     * @param href Address for the link
     * @return A valid link for this renderer
     */
    fun renderLink(text: String, href: String): String

    /**
     * Renders a list.
     *
     * Typically rendered as a bullet list.
     *
     * @param list List of already rendered texts
     * @return Valid list for the renderer
     */
    fun renderList(list: List<String>): String
}
