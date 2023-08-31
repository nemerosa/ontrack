package net.nemerosa.ontrack.model.structure

/**
 * Defines how validation runs should be sorted.
 */
enum class ValidationRunSortingMode {

    /**
     * Sorting by decreasing ID (the default), therefore having the most recent validation in front.
     */
    ID,

    /**
     * Sorting by decreasing run time, therefore having the slowest validations in front.
     */
    RUN_TIME,

    /**
     * Sorting by alphabetical order.
     */
    NAME,

}