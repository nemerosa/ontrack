package net.nemerosa.ontrack.extension.av.validation

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningTargetFileService
import net.nemerosa.ontrack.extension.av.settings.AutoVersioningSettings
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningValidationServiceImpl(
    private val autoVersionConfigurationService: AutoVersioningConfigurationService,
    private val structureService: StructureService,
    private val autoVersioningValidationDataType: AutoVersioningValidationDataType,
    private val buildDisplayNameService: BuildDisplayNameService,
    private val scmDetector: SCMDetector,
    private val autoVersioningTargetFileService: AutoVersioningTargetFileService,
    private val buildFilterService: BuildFilterService,
    private val cachedSettingsService: CachedSettingsService,
) : AutoVersioningValidationService {

    override fun checkAndValidate(build: Build): List<AutoVersioningValidationData> {
        val config = autoVersionConfigurationService.getAutoVersioning(build.branch)
        return config?.configurations
            ?.filter { !it.validationStamp.isNullOrBlank() }
            ?.mapNotNull { checkAndValidate(build, it) }
            ?: emptyList()
    }

    private fun checkAndValidate(build: Build, config: AutoVersioningSourceConfig): AutoVersioningValidationData? {
        // Validation stamp name
        val validationStampName = getActualValidationStampName(config)
        return if (!validationStampName.isNullOrBlank()) {
            // Gets the version information
            val start = System.currentTimeMillis()
            val time: Long
            val (current, last) = try {
                getVersionInfo(build, config)
            } finally {
                time = System.currentTimeMillis() - start
            }
            // Validation
            val data = AutoVersioningValidationData(
                project = config.sourceProject,
                version = current ?: "",
                latestVersion = last ?: "",
                path = config.targetPath,
                time = time
            )
            // Validation stamp creation with type
            val validationStamp = structureService.getOrCreateValidationStamp(build.branch, validationStampName)
            if (validationStamp.dataType?.descriptor?.id != autoVersioningValidationDataType.descriptor.id) {
                structureService.saveValidationStamp(
                    validationStamp.withDataType(
                        ValidationDataTypeConfig(
                            autoVersioningValidationDataType.descriptor,
                            null
                        )
                    )
                )
            }
            // Validation of the build
            structureService.newValidationRun(
                build,
                ValidationRunRequest(
                    validationStampName = validationStampName,
                    data = data
                )
            )
            // Creation of the build link
            val settings = cachedSettingsService.getCachedSettings(AutoVersioningSettings::class.java)
            if (settings.buildLinks && current != null && (config.buildLinkCreation == null || config.buildLinkCreation)) {
                if (!structureService.isLinkedTo(build, config.sourceProject, "*")) {
                    // Source project
                    val sourceProject = structureService.findProjectByName(config.sourceProject).getOrNull()
                    if (sourceProject != null) {
                        // Looking for the target build based its name first
                        val targetBuild = structureService.buildSearch(
                            sourceProject.id,
                            BuildSearchForm(
                                maximumCount = 1,
                                buildName = current,
                                buildExactMatch = true,
                            )
                        ).firstOrNull()
                        // ... then on its label
                            ?: structureService.buildSearch(
                                sourceProject.id,
                                BuildSearchForm(
                                    maximumCount = 1,
                                    property = ReleasePropertyType::class.java.name,
                                    propertyValue = current,
                                )
                            ).firstOrNull()
                        // Creation of the link
                        if (targetBuild != null) {
                            structureService.addBuildLink(
                                fromBuild = build,
                                toBuild = targetBuild,
                            )
                        }
                    }
                }
            }
            // OK
            data
        } else {
            null
        }
    }

    private fun getActualValidationStampName(config: AutoVersioningSourceConfig): String? =
        if (config.validationStamp.isNullOrBlank()) {
            null
        } else if (config.validationStamp == "auto") {
            "auto-versioning-${NameDescription.escapeName(config.sourceProject)}"
        } else {
            config.validationStamp
        }

    private fun getVersionInfo(build: Build, config: AutoVersioningSourceConfig) = VersionInfo(
        getCurrentVersion(build, config),
        getLastVersion(config)
    )

    private fun getCurrentVersion(build: Build, config: AutoVersioningSourceConfig): String? {
        // Using build links first
        val link = structureService.getBuildsUsedBy(build, 0, 1) {
            it.project.name == config.sourceProject
        }.pageItems.firstOrNull()
        val linkedVersion = link?.run { buildDisplayNameService.getBuildDisplayName(this) }
        return linkedVersion ?: readCurrentVersion(build.branch, config)
    }

    private fun readCurrentVersion(branch: Branch, config: AutoVersioningSourceConfig): String? {
        val scm = scmDetector.getSCM(branch.project) ?: return null
        val scmBranch: String = scm.getSCMBranch(branch) ?: return null
        // Using the first path only
        val targetPath = config.getTargetPaths().first()
        val lines = scm.download(scmBranch, targetPath)
            ?.toString(Charsets.UTF_8)
            ?.lines()
            ?: emptyList()
        return autoVersioningTargetFileService.readVersion(config, lines)
    }

    private fun getLastVersion(config: AutoVersioningSourceConfig): String? {
        // Gets the source project
        val sourceProject = structureService.findProjectByName(config.sourceProject).getOrNull()
        // Gets the latest eligible branch for the source project
        val sourceBranch = sourceProject?.run { autoVersionConfigurationService.getLatestBranch(sourceProject, config) }
        // Gets the latest build for this branch having the corresponding promotion
        val sourceBuild = sourceBranch?.run {
            buildFilterService.standardFilterProviderData(1)
                .withWithPromotionLevel(config.sourcePromotion)
                .build()
                .filterBranchBuilds(sourceBranch)
                .firstOrNull()
        }
        // Gets its version
        return sourceBuild?.run { buildDisplayNameService.getBuildDisplayName(sourceBuild) }
    }

}