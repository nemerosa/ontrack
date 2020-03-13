package net.nemerosa.ontrack.model.support

/**
 * Configuration properties for the search
 */
class SearchConfigProperties {

    companion object {
        /**
         * Engine property
         */
        const val ENGINE_PROPERTY = "engine"
    }

    /**
     * Search engine to be used.
     */
    var engine = OntrackConfigProperties.SEARCH

    /**
     * Index configuration
     */
    var index = SearchIndexProperties()

    class SearchIndexProperties {
        /**
         * Flag to enable immediate re-indexation after items are added into the search index (used mostly
         * for testing).
         */
        var immediate = false
        /**
         * Number of items to include in a batch when re-indexing a whole collection.
         *
         * Note that this can be overridden by the individual search indexers.
         */
        var batch = 1000
        /**
         * Logging mode for batch indexing (only actual actions are shown)
         */
        var logging = false
        /**
         * Tracing mode for batch indexing (generates an awful lot of logging at DEBUG level)
         */
        var tracing = false
    }
}