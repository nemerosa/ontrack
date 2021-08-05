package net.nemerosa.ontrack.extension.indicators.computing

interface ConfigurableIndicatorService {

    /**
     * Gets the stored state for a given configurable indicator type
     *
     * @param type Type to get the state for
     * @return State for this configurable state or null if none has been stored
     */
    fun getConfigurableIndicatorState(type: ConfigurableIndicatorType<*, *>): ConfigurableIndicatorState?

    /**
     * Saves the state for a given configurable indicator type
     *
     * @param type Type to save the state for
     * @param state State to save (null to remove)
     */
    fun saveConfigurableIndicator(type: ConfigurableIndicatorType<*, *>, state: ConfigurableIndicatorState?)

    /**
     * List of all configurable indicators and their states.
     */
    fun getConfigurableIndicatorStates(): List<ConfigurableIndicatorTypeState<*,*>>
}