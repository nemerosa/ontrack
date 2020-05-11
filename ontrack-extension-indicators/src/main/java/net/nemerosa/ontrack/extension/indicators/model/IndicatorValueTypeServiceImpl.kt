package net.nemerosa.ontrack.extension.indicators.model

import org.springframework.stereotype.Service

@Service
class IndicatorValueTypeServiceImpl(
        indicatorValueTypes: List<IndicatorValueType<*, *>>
) : IndicatorValueTypeService {

    private val index = indicatorValueTypes.associateBy { it.id }

    override fun findAll(): List<IndicatorValueType<*, *>> = index.values.sortedBy { it.name }

    override fun findValueTypeById(id: String): IndicatorValueType<*, *>? = index[id]

}