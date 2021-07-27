package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorPortfolioService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.nullableType
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Report on a category
 */
@Component
class GQLTypeIndicatorCategoryReport(
    private val indicatorCategoryReportProject: GQLTypeIndicatorCategoryReportProject,
    private val indicatorCategoryReportType: GQLTypeIndicatorCategoryReportType,
    private val indicatorReportingService: IndicatorReportingService,
    private val structureService: StructureService,
    private val labelManagementService: LabelManagementService,
    private val projectLabelManagementService: ProjectLabelManagementService,
    private val portfolioService: IndicatorPortfolioService,
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorService: IndicatorService,
) : GQLType {

    override fun getTypeName(): String = INDICATOR_CATEGORY_REPORT

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Report of indicators for a category")
            // Reports per projects
            .field {
                it.name(IndicatorCategoryReport::projectReport.name)
                    .description("Report of indicators per project")
                    .type(listType(indicatorCategoryReportProject.typeRef))
            }
            // Reports per type
            .field {
                it.name(IndicatorCategoryReport::typeReport.name)
                    .description("Report of indicators per type")
                    .type(listType(indicatorCategoryReportType.typeRef))
            }
            // OK
            .build()

    fun report(category: IndicatorCategory, env: DataFetchingEnvironment) =
        IndicatorCategoryReport(category, env)

    companion object {
        const val ARG_FILLED_ONLY = "filledOnly"
        const val ARG_PROJECT_ID = "projectId"
        const val ARG_PROJECT_NAME = "projectName"
        const val ARG_PORTFOLIO = "portfolio"
        const val ARG_LABEL = "label"

        const val INDICATOR_CATEGORY_REPORT: String = "IndicatorCategoryReport"
    }

    inner class IndicatorCategoryReport(
        private val category: IndicatorCategory,
        private val reportEnv: DataFetchingEnvironment,
    ) {

        val projectReport: List<GQLTypeIndicatorCategoryReportProject.IndicatorCategoryReportProject> by lazy {
            projects.map { project ->
                GQLTypeIndicatorCategoryReportProject.IndicatorCategoryReportProject(
                    project = project,
                    indicators = types.map { type ->
                        indicatorService.getProjectIndicator(project, type).run {
                            ProjectIndicator(project, this)
                        }
                    }
                )
            }
        }

        val typeReport: List<GQLTypeIndicatorCategoryReportType.IndicatorCategoryReportType> by lazy {
            types.map { type ->
                GQLTypeIndicatorCategoryReportType.IndicatorCategoryReportType(
                    type = type,
                    projectIndicators = projects.map { project ->
                        GQLTypeIndicatorCategoryReportTypeEntry.IndicatorCategoryReportTypeEntry(
                            project = project,
                            indicator = ProjectIndicator(
                                project,
                                indicatorService.getProjectIndicator(project, type)
                            )
                        )
                    }
                )
            }
        }

        private val types: List<IndicatorType<*, *>> by lazy {
            indicatorTypeService.findByCategory(category)
        }

        private val projects: List<Project> by lazy {
            indicatorReportingService.findProjects(reportEnv, types)
        }

    }

}

@Component
class GQLTypeIndicatorCategoryReportProject(
    private val projectIndicator: GQLTypeProjectIndicator
) : GQLType {

    class IndicatorCategoryReportProject(
        val project: Project,
        val indicators: List<ProjectIndicator>
    )

    override fun getTypeName(): String = "IndicatorCategoryReportProject"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Report of indicators for one project")
            // Associated project
            .field {
                it.name("project")
                    .description("Associated project")
                    .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
            }
            // Indicators
            .field {
                it.name("indicators")
                    .description("Indicators for one projects and types in this category")
                    .type(listType(projectIndicator.typeRef))
            }
            // OK
            .build()

}

@Component
class GQLTypeIndicatorCategoryReportType(
    private val indicatorType: GQLTypeProjectIndicatorType,
    private val indicatorCategoryReportTypeEntryType: GQLTypeIndicatorCategoryReportTypeEntry
) : GQLType {

    class IndicatorCategoryReportType(
        val type: IndicatorType<*, *>,
        val projectIndicators: List<GQLTypeIndicatorCategoryReportTypeEntry.IndicatorCategoryReportTypeEntry>
    )

    override fun getTypeName(): String = "IndicatorCategoryReportType"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Report of indicators for one type")
            // Associated type
            .field {
                it.name("type")
                    .description("Associated type")
                    .type(indicatorType.typeRef)
            }
            // Indicator & their project
            .field {
                it.name("projectIndicators")
                    .description("Indicators for one projects and types in this category")
                    .type(listType(indicatorCategoryReportTypeEntryType.typeRef))
            }
            // OK
            .build()

}

@Component
class GQLTypeIndicatorCategoryReportTypeEntry(
    private val projectIndicator: GQLTypeProjectIndicator
) : GQLType {

    class IndicatorCategoryReportTypeEntry(
        val project: Project,
        val indicator: ProjectIndicator
    )

    override fun getTypeName(): String = "IndicatorCategoryReportTypeEntry"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Indicator for one project and one type")
            // Associated project
            .field {
                it.name("project")
                    .description("Associated project")
                    .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
            }
            // Indicator
            .field {
                it.name("indicator")
                    .description("Indicator for one project and one type")
                    .type(nullableType(projectIndicator.typeRef, nullable = false))
            }
            // OK
            .build()

}