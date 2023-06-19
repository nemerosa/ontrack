package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service

@Service
class TFCBuildServiceImpl(
        private val structureService: StructureService,
        private val propertyService: PropertyService,
        private val buildFilterService: BuildFilterService,
) : TFCBuildService {
    override fun findBuild(params: TFCParameters): Build? {
        // Working at project level
        if (params.branch.isNullOrBlank()) {
            // The build parameter is required
            if (params.build.isNullOrBlank()) {
                throw TFCMissingParameterException(TFCParameters::build.name)
            } else {
                // Gets the project
                val project = structureService.findProjectByName(params.project).getOrNull()
                        ?: return null
                // Is the project using build labels?
                val property = propertyService.getPropertyValue(project, BuildLinkDisplayPropertyType::class.java)
                return if (property != null && property.useLabel) {
                    structureService.buildSearch(project.id, BuildSearchForm(
                            maximumCount = 1,
                            property = ReleasePropertyType::class.java.name,
                            propertyValue = params.build,
                    )).firstOrNull()
                }
                // Build by name
                else {
                    structureService.buildSearch(project.id, BuildSearchForm(
                            maximumCount = 1,
                            buildName = params.build,
                            buildExactMatch = true,
                    )).firstOrNull()
                }
            }
        }
        // Working at branch level
        else {
            // Escaping the branch name
            val branchName = NameDescription.escapeName(params.branch)
            // Gets the branch first
            val branch = structureService.findBranchByName(params.project, branchName).getOrNull()
                    ?: return null
            // If build is not set, we use the latest build
            if (params.build.isNullOrBlank()) {
                return if (params.promotion.isNullOrBlank()) {
                    // Latest build
                    buildFilterService.standardFilterProviderData(1)
                            .build()
                            .filterBranchBuilds(branch)
                            .firstOrNull()
                } else {
                    // Latest promoted build
                    buildFilterService.standardFilterProviderData(1)
                            .withWithPromotionLevel(params.promotion)
                            .build()
                            .filterBranchBuilds(branch)
                            .firstOrNull()
                }
            } else {
                // Using the build label
                val property = propertyService.getPropertyValue(branch.project, BuildLinkDisplayPropertyType::class.java)
                return if (property != null && property.useLabel) {
                    buildFilterService.standardFilterProviderData(1)
                            .withWithProperty(ReleasePropertyType::class.java.name)
                            .withWithPropertyValue(params.build)
                            .build()
                            .filterBranchBuilds(branch)
                            .firstOrNull()
                }
                // ... or the build name
                else {
                    structureService.findBuildByName(params.project, branchName, params.build).getOrNull()
                }
            }
        }
    }
}