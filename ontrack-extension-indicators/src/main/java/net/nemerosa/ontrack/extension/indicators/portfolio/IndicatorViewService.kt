package net.nemerosa.ontrack.extension.indicators.portfolio

/**
 * Management of [IndicatorView]s.
 */
interface IndicatorViewService {

    /**
     * Gets the list of indicator views
     */
    fun getIndicatorViews(): List<IndicatorView>

    /**
     * Creates or updates an indicator view
     */
    fun saveIndicatorView(view: IndicatorView)

    /**
     * Gets a view using its name
     */
    fun findIndicatorViewByName(name: String): IndicatorView?

    /**
     * Deletes an indicator view
     */
    fun deleteIndicatorView(name: String)

}