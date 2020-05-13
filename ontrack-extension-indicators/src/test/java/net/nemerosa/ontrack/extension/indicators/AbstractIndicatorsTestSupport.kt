package net.nemerosa.ontrack.extension.indicators

import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolio
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.extension.indicators.portfolio.PortfolioUpdateForm
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractIndicatorsTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var booleanIndicatorValueType: BooleanIndicatorValueType

    @Autowired
    protected lateinit var indicatorCategoryService: IndicatorCategoryService

    @Autowired
    protected lateinit var indicatorTypeService: IndicatorTypeService

    @Autowired
    protected lateinit var indicatorService: IndicatorService

    @Autowired
    protected lateinit var indicatorPortfolioService: IndicatorPortfolioService

    protected fun clearIndicators() {
        asAdmin {
            indicatorCategoryService.findAll().forEach { category ->
                indicatorCategoryService.deleteCategory(category.id)
            }
        }
    }

    protected fun category(id: String = uid("C"), name: String = id) = asAdmin {
        indicatorCategoryService.createCategory(
                IndicatorForm(
                        id,
                        name
                )
        )
    }

    protected fun IndicatorCategory.booleanType(
            id: String = uid("T"),
            name: String = "$id description",
            required: Boolean = true
    ): IndicatorType<Boolean, BooleanIndicatorValueTypeConfig> = asAdmin {
        indicatorTypeService.createType(
                id = id,
                category = this,
                shortName = id,
                longName = name,
                link = null,
                valueType = booleanIndicatorValueType,
                valueConfig = BooleanIndicatorValueTypeConfig(required)
        )
    }

    protected fun <T> Project.indicator(
            type: IndicatorType<T, *>,
            value: T?
    ) {
        asAdmin {
            indicatorService.updateProjectIndicator(
                    project = project,
                    type = type,
                    value = value,
                    comment = null
            )
        }
    }

    protected fun portfolio(
            categories: List<IndicatorCategory> = emptyList()
    ): IndicatorPortfolio = asAdmin {
        val id = uid("P")
        val portfolio = indicatorPortfolioService.createPortfolio(
                id = id,
                name = "$id portfolio"
        )
        if (categories.isNotEmpty()) {
            indicatorPortfolioService.updatePortfolio(
                    id,
                    PortfolioUpdateForm(
                            categories = categories.map { it.id }
                    )
            )
        }
        portfolio
    }

}