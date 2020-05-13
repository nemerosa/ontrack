package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractIndicatorsTestSupport : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var booleanIndicatorValueType: BooleanIndicatorValueType

    @Autowired
    private lateinit var indicatorCategoryService: IndicatorCategoryService

    @Autowired
    private lateinit var indicatorTypeService: IndicatorTypeService

    @Autowired
    private lateinit var indicatorService: IndicatorService

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

}