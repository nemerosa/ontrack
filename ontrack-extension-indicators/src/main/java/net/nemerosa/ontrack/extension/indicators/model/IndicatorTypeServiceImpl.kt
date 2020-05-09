package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import org.springframework.stereotype.Service

@Service
class IndicatorTypeServiceImpl(
        private val indicatorCategoryService: IndicatorCategoryService,
        private val booleanIndicatorValueType: BooleanIndicatorValueType
) : IndicatorTypeService {

    private val types: Map<String, IndicatorType<*, *>> = listOf(
            IndicatorType(
                    id = "36083450-cacb-4e30-bbed-fd6682f336fd",
                    category = indicatorCategoryService.getCategory(IndicatorCategoryServiceImpl.SERVICES),
                    shortName = "Java stack",
                    longName = "SHOULD Use Java & spring boot stack",
                    link = "https://start.spring.io",
                    valueType = booleanIndicatorValueType,
                    valueConfig = BooleanIndicatorValueTypeConfig(required = false),
                    valueComputer = null
            ),
            IndicatorType(
                    id = "7291c0ba-a038-4ca4-907e-364b57cba8bd",
                    category = indicatorCategoryService.getCategory(IndicatorCategoryServiceImpl.SERVICES),
                    shortName = "Java 11 Zulu",
                    longName = "MUST Use zulu JDK 11 LTS for JVM services",
                    link = null,
                    valueType = booleanIndicatorValueType,
                    valueConfig = BooleanIndicatorValueTypeConfig(required = true),
                    valueComputer = null
            ),
            IndicatorType(
                    id = "91f0ac82-0442-4ae0-9995-ed2e8fc9e6f9",
                    category = indicatorCategoryService.getCategory(IndicatorCategoryServiceImpl.DELIVERY),
                    shortName = "Docker name",
                    longName = "MUST follow Docker artifact naming conventions",
                    link = null,
                    valueType = booleanIndicatorValueType,
                    valueConfig = BooleanIndicatorValueTypeConfig(required = true),
                    valueComputer = null
            )
    ).associateBy { it.id }

    override fun findAll(): List<IndicatorType<*, *>> {
        return types.values.sortedWith(
                compareBy(
                        { it.category.name },
                        { it.shortName }
                )
        )
    }

    override fun findTypeById(typeId: String): IndicatorType<*, *>? =
            types[typeId]

    override fun getTypeById(typeId: String): IndicatorType<*, *> =
            findTypeById(typeId) ?: throw IndicatorTypeNotFoundException(typeId)
}