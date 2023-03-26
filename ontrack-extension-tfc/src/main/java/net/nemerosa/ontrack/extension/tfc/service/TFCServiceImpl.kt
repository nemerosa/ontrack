package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType
import net.nemerosa.ontrack.extension.tfc.client.TFCClientFactory
import net.nemerosa.ontrack.extension.tfc.config.TFCConfigurationService
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service

@Service
class TFCServiceImpl(
    private val structureService: StructureService,
    private val securityService: SecurityService,
    private val propertyService: PropertyService,
    private val buildFilterService: BuildFilterService,
    private val tfcConfigurationService: TFCConfigurationService,
    private val tfcClientFactory: TFCClientFactory,
) : TFCService {

    override fun validate(
        params: TFCParameters,
        status: ValidationRunStatusID,
        workspaceId: String,
        runUrl: String
    ): TFCValidationResult {
        // Getting the actual parameters
        val actualParams = expandParams(params, workspaceId, runUrl)
        return securityService.asAdmin {
            // Looking for the build
            val build = findBuild(actualParams) ?: return@asAdmin TFCValidationResult(
                params, null, null
            )
            // Forcing the creation of the validation stamp if not existing
            val stamp = structureService.findValidationStampByName(
                params.project, params.branch, params.validation
            ).getOrNull()
            if (stamp == null) {
                structureService.newValidationStamp(
                    ValidationStamp.of(
                        build.branch,
                        NameDescription.nd(params.validation, "")
                    )
                )
            }
            // Validation
            val run = structureService.newValidationRun(
                build, ValidationRunRequest(
                    validationStampName = params.validation,
                    validationRunStatusId = status,
                )
            )
            // OK
            TFCValidationResult(params, build, run)
        }
    }

    private fun findBuild(params: TFCParameters): Build? {
        // Gets the branch first
        val branch = structureService.findBranchByName(params.project, params.branch).getOrNull()
            ?: return null
        // Using the build label
        val property = propertyService.getPropertyValue(branch.project, BuildLinkDisplayPropertyType::class.java)
        return if (property != null && property.useLabel) {
            buildFilterService.standardFilterProviderData(1)
                .withWithProperty(BuildLinkDisplayPropertyType::class.java.name)
                .withWithPropertyValue(params.build)
                .build()
                .filterBranchBuilds(branch)
                .firstOrNull()
        }
        // ... or the build name
        else {
            structureService.findBuildByName(params.project, params.branch, params.build).getOrNull()
        }
    }

    private fun expandParams(params: TFCParameters, workspaceId: String, runUrl: String): TFCParameters =
        if (params.hasVariables()) {
            // Gets the list of variables from TFC
            val variables = getWorkspaceVariables(workspaceId, runUrl)
            // Expands the parameters using these variables
            params.expand(variables, workspaceId)
        } else {
            params
        }

    private fun getWorkspaceVariables(workspaceId: String, runUrl: String): Map<String, String> {
        // Gets a configuration using the URL
        val config = tfcConfigurationService.findConfigurationByURL(runUrl)
            ?: throw TFCNoConfigurationException(runUrl)
        // Creating a client
        val client = tfcClientFactory.createClient(config)
        // Getting the list of variables for the workspace
        val variables = client.getWorkspaceVariables(workspaceId)
        // Indexing
        return variables.associate { v ->
            v.key to (v.value ?: "")
        }
    }

}