package net.nemerosa.ontrack.extension.indicators.model

interface IndicatorTypeService {

    fun findAll(): List<IndicatorType<*, *>>

}