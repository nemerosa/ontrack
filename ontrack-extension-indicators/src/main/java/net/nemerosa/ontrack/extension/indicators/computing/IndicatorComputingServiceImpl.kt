package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.model.*
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.SyncConfig
import net.nemerosa.ontrack.model.structure.SyncPolicy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorComputingServiceImpl(
        private val indicatorCategoryService: IndicatorCategoryService,
        private val indicatorTypeService: IndicatorTypeService,
        private val indicatorService: IndicatorService
) : IndicatorComputingService {

    override fun compute(computer: IndicatorComputer, project: Project) {
        // Gets the source
        val source = computer.source
        // Gets the indicators
        val indicators = computer.computeIndicators(project)

        // Preparing the categories & types
        prepareTypes(source, indicators)

        // Importing the project indicators
        indicators.forEach { indicator ->
            saveIndicator(project, indicator)
        }
    }

    private fun <T, C> saveIndicator(project: Project, indicator: IndicatorComputedValue<T, C>) {
        @Suppress("UNCHECKED_CAST")
        val type = indicatorTypeService.getTypeById(indicator.type.id) as IndicatorType<T, C>
        // Gets current value
        val current = indicatorService.getProjectIndicator(project, type)
        // If value is different, then stores the value value
        if (indicator.value != current.value) {
            indicatorService.updateProjectIndicator(
                    project,
                    type,
                    indicator.value,
                    indicator.comment
            )
        }
    }

    private fun prepareTypes(source: IndicatorSource, indicators: List<IndicatorComputedValue<*, *>>) {
        val types = indicators.map { it.type }
        val categories = types.map { it.category }.distinctBy { it.id }

        SyncPolicy.SYNC.sync(object : SyncConfig<IndicatorComputedCategory, String> {

            override fun getItemType(): String = "Category"

            override fun getSourceItems(): Collection<IndicatorComputedCategory> = categories

            override fun getTargetItems(): Collection<IndicatorComputedCategory> =
                    indicatorCategoryService.findAll()
                            .filter { source sameAs it.source }
                            .map {
                                IndicatorComputedCategory(it.id, it.name)
                            }

            override fun getItemId(item: IndicatorComputedCategory): String = item.id

            override fun createTargetItem(sourceCategory: IndicatorComputedCategory) {
                indicatorCategoryService.createCategory(
                        IndicatorForm(
                                sourceCategory.id,
                                sourceCategory.name
                        ),
                        source
                )
            }

            override fun replaceTargetItem(sourceCategory: IndicatorComputedCategory, target: IndicatorComputedCategory) {
                indicatorCategoryService.updateCategory(
                        IndicatorForm(
                                sourceCategory.id,
                                sourceCategory.name
                        ),
                        source
                )
            }

            override fun deleteTargetItem(target: IndicatorComputedCategory) {
                indicatorCategoryService.deleteCategory(target.id)
            }

        })

        SyncPolicy.SYNC.sync(object : SyncConfig<IndicatorComputedType<*, *>, String> {

            override fun getItemType(): String = "Type"

            override fun getSourceItems(): Collection<IndicatorComputedType<*, *>> = types

            override fun getTargetItems(): Collection<IndicatorComputedType<*, *>> =
                    indicatorTypeService.findAll()
                            .filter { source sameAs it.source }
                            .map { toIndicatorComputedType(it) }

            override fun getItemId(item: IndicatorComputedType<*, *>): String = item.id

            override fun createTargetItem(type: IndicatorComputedType<*, *>) {

                fun <T, C> createType(type: IndicatorComputedType<T, C>) {
                    indicatorTypeService.createType(
                            type.id,
                            indicatorCategoryService.getCategory(type.category.id),
                            type.name,
                            type.link,
                            type.valueType,
                            type.valueConfig,
                            source,
                            computed = true
                    )
                }

                createType(type)
            }

            override fun replaceTargetItem(type: IndicatorComputedType<*, *>, target: IndicatorComputedType<*, *>) {

                fun <T, C> updateType(type: IndicatorComputedType<T, C>) {
                    indicatorTypeService.updateType(
                            type.id,
                            indicatorCategoryService.getCategory(type.category.id),
                            type.name,
                            type.link,
                            type.valueType,
                            type.valueConfig,
                            source,
                            computed = true
                    )
                }

                updateType(type)
            }

            override fun deleteTargetItem(target: IndicatorComputedType<*, *>) {
                indicatorTypeService.deleteType(target.id)
            }

        })

    }

    private fun <T, C> toIndicatorComputedType(it: IndicatorType<T, C>): IndicatorComputedType<T, C> {
        return IndicatorComputedType(
                category = IndicatorComputedCategory(
                        it.category.id,
                        it.category.name
                ),
                id = it.id,
                name = it.id,
                link = it.link,
                valueType = it.valueType,
                valueConfig = it.valueConfig
        )
    }

}