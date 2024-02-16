package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity

interface EventRenderer {

    /**
     * ID of this renderer
     */
    val id: String

    /**
     * Display name for this renderer
     */
    val name: String

    /**
     * Rendering the link to an entity.
     *
     * @param projectEntity Entity to render
     * @param name Name to use
     */
    fun render(projectEntity: ProjectEntity, name: String): String

    /**
     * Renders a value and gives it some emphasis.
     */
    fun renderStrong(value: String): String

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

    /**
     * Renders a separator between two sections.
     */
    fun renderSpace(): String

    /**
     * Renders a section with a [title] and some [content].
     */
    fun renderSection(title: String, content: String): String
}
