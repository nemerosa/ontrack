package net.nemerosa.ontrack.extension.indicators.model

import org.springframework.stereotype.Service

@Service
class IndicatorValueTypeServiceImpl(
        indicatorValueTypes: List<IndicatorValueType<*, *>>
) : IndicatorValueTypeService {

    private val index = indicatorValueTypes.associateBy { it.id }

    override fun findAll(): List<IndicatorValueType<*, *>> = index.values.sortedBy { it.name }

    @Suppress("UNCHECKED_CAST")
    override fun <T, C> findValueTypeById(id: String): IndicatorValueType<T, C>? = index[id] as IndicatorValueType<T, C>?

    override fun <T, C> getValueType(id: String): IndicatorValueType<T, C> = findValueTypeById(id)
            ?: throw IndicatorValueTypeNotFoundException(id)

}