package net.nemerosa.ontrack.extension.indicators

import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.portfolio.*
import net.nemerosa.ontrack.extension.indicators.support.IntegerThresholds
import net.nemerosa.ontrack.extension.indicators.support.Percentage
import net.nemerosa.ontrack.extension.indicators.support.PercentageThreshold
import net.nemerosa.ontrack.extension.indicators.support.percent
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.extension.indicators.values.IntegerIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.PercentageIndicatorValueType
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

abstract class AbstractIndicatorsTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var booleanIndicatorValueType: BooleanIndicatorValueType

    @Autowired
    protected lateinit var integerIndicatorValueType: IntegerIndicatorValueType

    @Autowired
    protected lateinit var percentageIndicatorValueType: PercentageIndicatorValueType

    @Autowired
    protected lateinit var indicatorCategoryService: IndicatorCategoryService

    @Autowired
    protected lateinit var indicatorTypeService: IndicatorTypeService

    @Autowired
    protected lateinit var indicatorService: IndicatorService

    @Autowired
    protected lateinit var indicatorPortfolioService: IndicatorPortfolioService

    @Autowired
    protected lateinit var indicatorViewService: IndicatorViewService

    protected fun clearIndicators() {
        asAdmin {
            indicatorCategoryService.findAll().forEach { category ->
                indicatorCategoryService.deleteCategory(category.id)
            }
        }
    }

    protected fun clearPortfolios() {
        asAdmin {
            indicatorPortfolioService.findAll().forEach { portfolio ->
                indicatorPortfolioService.deletePortfolio(portfolio.id)
            }
        }
    }

    protected fun category(
        id: String = uid("C"),
        name: String = id,
        source: IndicatorSource? = null,
        deprecated: String? = null
    ) = asAdmin {
        indicatorCategoryService.createCategory(
                IndicatorForm(
                        id,
                        name,
                        deprecated = deprecated
                ),
                source = source
        )
    }

    protected fun indicatorView(
        name: String = uid("V"),
        categories: List<IndicatorCategory>,
    ) = asAdmin {
        indicatorViewService.saveIndicatorView(
            IndicatorView(
                id = "",
                name = name,
                categories = categories.map { it.id }
            )
        )
    }

    protected fun source() = IndicatorSource(
        IndicatorSourceProviderDescription("test-provider", "Provider for test"),
        "test"
    )

    protected fun IndicatorCategory.booleanType(
            id: String = uid("T"),
            name: String = "$id description",
            required: Boolean = true,
            source: IndicatorSource? = null,
            deprecated: String? = null
    ): IndicatorType<Boolean, BooleanIndicatorValueTypeConfig> = asAdmin {
        indicatorTypeService.createType(
                id = id,
                category = this,
                name = name,
                link = null,
                valueType = booleanIndicatorValueType,
                valueConfig = BooleanIndicatorValueTypeConfig(required),
                source = source,
                deprecated = deprecated
        )
    }

    protected fun IndicatorCategory.integerType(
            id: String = uid("T"),
            name: String = "$id description",
            min: Int = 0,
            max: Int = 10,
            higherIsBetter: Boolean = false,
            source: IndicatorSource? = null
    ): IndicatorType<Int, IntegerThresholds> = asAdmin {
        indicatorTypeService.createType(
                id = id,
                category = this,
                name = name,
                link = null,
                valueType = integerIndicatorValueType,
                valueConfig = IntegerThresholds(min, max, higherIsBetter),
                source = source
        )
    }

    protected fun IndicatorCategory.percentageType(
            id: String = uid("T"),
            name: String = "$id description",
            threshold: Int = 50,
            higherIsBetter: Boolean = true,
            source: IndicatorSource? = null
    ): IndicatorType<Percentage, PercentageThreshold> = asAdmin {
        indicatorTypeService.createType(
                id = id,
                category = this,
                name = name,
                link = null,
                valueType = percentageIndicatorValueType,
                valueConfig = PercentageThreshold(threshold.percent(), higherIsBetter),
                source = source
        )
    }

    protected fun <T> Project.indicator(
            type: IndicatorType<T, *>,
            value: T?,
            time: LocalDateTime? = null,
            comment: String? = null
    ) {
        asAdmin {
            indicatorService.updateProjectIndicator(
                    project = project,
                    type = type,
                    value = value,
                    comment = comment,
                    time = time
            )
        }
    }

    protected fun <T> Project.checkIndicator(
            type: IndicatorType<T, *>,
            code: (Indicator<T>) -> Unit
    ) {
        asAdmin {
            val indicator = indicatorService.getProjectIndicator(project, type)
            code(indicator)
        }
    }

    protected fun <T : Any> Project.assertIndicatorValue(
            type: IndicatorType<T, *>,
            code: (T) -> Unit = {}
    ) {
        checkIndicator(type) { i ->
            val indicatorValue = i.value
            assertNotNull(indicatorValue, "Indicator value must be set") { value: T ->
                code(value)
            }
        }
    }

    protected fun <T : Any> Project.assertIndicatorValueIs(
            type: IndicatorType<T, *>,
            expectedValue: T?
    ) {
        checkIndicator(type) { i ->
            val indicatorValue = i.value
            assertEquals(expectedValue, indicatorValue)
        }
    }

    protected fun <T> Project.assertIndicatorNoValue(
            type: IndicatorType<T, *>
    ) {
        checkIndicator(type) { i ->
            assertNull(i.value, "Indicator value must not be set")
        }
    }

    protected fun portfolio(
            id: String = uid("P"),
            name: String = "$id portfolio",
            categories: List<IndicatorCategory> = emptyList(),
            label: Label? = null
    ): IndicatorPortfolio = asAdmin {
        var portfolio = indicatorPortfolioService.createPortfolio(id = id, name = name)
        if (categories.isNotEmpty() || label != null) {
            portfolio = indicatorPortfolioService.updatePortfolio(
                    id,
                    PortfolioUpdateForm(
                            categories = categories.map { it.id },
                            label = label?.id
                    )
            )
        }
        portfolio
    }

}