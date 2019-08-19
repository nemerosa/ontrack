package net.nemerosa.ontrack.ui.resource

/**
 * Marks a class as delegating the computation of the links to a field.
 */
interface ResourceDecoratorDelegate {

    /**
     * Returns the object to use when computing the links
     */
    fun getLinkDelegate(): Any

}
