package net.nemerosa.ontrack.extension.indicators.imports

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueType
import net.nemerosa.ontrack.extension.indicators.values.BooleanIndicatorValueTypeConfig
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorImportsServiceImpl(
        private val securityService: SecurityService,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val indicatorTypeService: IndicatorTypeService,
        private val booleanIndicatorValueType: BooleanIndicatorValueType,
        private val importsIndicatorSourceProvider: ImportsIndicatorSourceProvider
) : IndicatorImportsService {

    override fun imports(data: IndicatorImports) {
        securityService.checkGlobalFunction(IndicatorTypeManagement::class.java)
        val source = importsIndicatorSourceProvider.createSource(data.source)
        data.categories.forEach { categoryData ->
            val category = importCategory(categoryData, source)
            categoryData.types.forEach { typeData ->
                importType(category, typeData, source)
            }
        }
    }

    private fun importType(category: IndicatorCategory, typeData: IndicatorImportsType, source: IndicatorSource) {
        val type = indicatorTypeService.findTypeById(typeData.id)
        if (type != null) {
            indicatorTypeService.updateType(
                    id = typeData.id,
                    category = category,
                    name = typeData.name,
                    link = typeData.link,
                    valueType = booleanIndicatorValueType,
                    valueConfig = BooleanIndicatorValueTypeConfig(required = typeData.required ?: true),
                    source = source
            )
        } else {
            indicatorTypeService.createType(
                    id = typeData.id,
                    category = category,
                    name = typeData.name,
                    link = typeData.link,
                    valueType = booleanIndicatorValueType,
                    valueConfig = BooleanIndicatorValueTypeConfig(required = typeData.required ?: true),
                    source = source
            )
        }
    }

    private fun importCategory(categoryData: IndicatorImportCategory, source: IndicatorSource): IndicatorCategory {
        return if (indicatorCategoryService.findCategoryById(categoryData.id) != null) {
            indicatorCategoryService.updateCategory(
                    IndicatorForm(
                            id = categoryData.id,
                            name = categoryData.name
                    ),
                    source = source
            )
        } else {
            indicatorCategoryService.createCategory(
                    IndicatorForm(
                            id = categoryData.id,
                            name = categoryData.name
                    ),
                    source = source
            )
        }
    }

}