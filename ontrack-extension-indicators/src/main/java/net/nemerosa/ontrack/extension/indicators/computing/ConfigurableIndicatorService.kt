package net.nemerosa.ontrack.extension.indicators.computing

interface ConfigurableIndicatorService {

    /**
     * Gets the stored state for a given configurable indicator type
     *
     * @param type Type to get the state for
     * @return State for this configurable state or null if none has been stored
     */
    fun getConfigurableIndicatorState(type: ConfigurableIndicatorType<*, *>): ConfigurableIndicatorState?
}