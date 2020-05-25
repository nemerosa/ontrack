package net.nemerosa.ontrack.extension.indicators.model

interface IndicatorTypeListener {

    fun onTypeDeleted(type: IndicatorType<*, *>) {}

}