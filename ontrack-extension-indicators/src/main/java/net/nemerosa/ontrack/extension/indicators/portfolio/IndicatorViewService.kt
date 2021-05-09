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
     *
     * @param view View to create or update
     * @return Created or updated view
     */
    fun saveIndicatorView(view: IndicatorView): IndicatorView

    /**
     * Gets a view using its ID
     */
    fun findIndicatorViewById(id: String): IndicatorView?

    /**
     * Gets a view using its name
     */
    fun findIndicatorViewByName(name: String): IndicatorView?

    /**
     * Deletes an indicator view using its ID.
     */
    fun deleteIndicatorView(id: String)

}