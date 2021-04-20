package net.nemerosa.ontrack.model.links

/**
 * Indicates how the label of a decoration must be displayed.
 */
enum class BranchLinksDecorationLabel {

    /**
     * No label for the decoration
     */
    NONE,

    /**
     * Icon only, if available
     */
    ICON,

    /**
     * Text only, if available
     */
    TEXT,

    /**
     * Icon and text, if available
     */
    ALL

}