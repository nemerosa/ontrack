package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.job.Schedule

/**
 * Allows a [SearchProvider] to index some search data.
 */
interface SearchIndexer<T : SearchItem> {

    /**
     * ID of this indexer, used to uniquely identify this indexer.
     *
     * Defaults to the qualified class name of this indexer.
     */
    val id: String get() = this::class.java.name

    /**
     * Display name for this indexer.
     */
    val name: String

    /**
     * Is this indexer disabled?
     */
    val isDisabled: Boolean

    /**
     * Gets the indexation schedule for this indexer.
     *
     * By default, no schedule == manual indexation only
     */
    val schedule: Schedule get() = Schedule.NONE

    /**
     * Launching a full indexation
     */
    fun createFullIndex(): SearchIndex<T>

}