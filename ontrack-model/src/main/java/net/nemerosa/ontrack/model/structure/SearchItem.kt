package net.nemerosa.ontrack.model.structure

/**
 * Item which can be searched upon.
 */
interface SearchItem {

    /**
     * ID of this item
     */
    val id: String

    /**
     * Fields for this item
     */
    val fields: Map<String, Any>

}