package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import org.springframework.stereotype.Service

@Service
class IndicatorTypeServiceImpl(
        private val indicatorCategoryService: IndicatorCategoryService
) : IndicatorTypeService {

    private val types: Map<Int, IndicatorType<*, *>> = listOf(
            IndicatorType(
                    id = 1,
                    category = indicatorCategoryService.getCategory(IndicatorCategoryServiceImpl.SERVICES),
                    shortName = "Java stack",
                    longName = "SHOULD Use Java & spring boot stack",
                    link = null,
                    valueType = BooleanIndicatorValueType(),
                    valueConfig = BooleanIndicatorValueTypeConfig(required = false),
                    valueComputer = null
            ),
            IndicatorType(
                    id = 2,
                    category = indicatorCategoryService.getCategory(IndicatorCategoryServiceImpl.SERVICES),
                    shortName = "Java 11 Zulu",
                    longName = "MUST Use zulu JDK 11 LTS for JVM services",
                    link = null,
                    valueType = BooleanIndicatorValueType(),
                    valueConfig = BooleanIndicatorValueTypeConfig(required = true),
                    valueComputer = null
            ),
            IndicatorType(
                    id = 3,
                    category = indicatorCategoryService.getCategory(IndicatorCategoryServiceImpl.DELIVERY),
                    shortName = "Docker name",
                    longName = "MUST follow Docker artifact naming conventions",
                    link = null,
                    valueType = BooleanIndicatorValueType(),
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
}