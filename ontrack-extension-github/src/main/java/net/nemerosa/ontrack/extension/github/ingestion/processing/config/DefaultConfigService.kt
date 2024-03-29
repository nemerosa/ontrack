package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.casc.entities.CascEntityService
import net.nemerosa.ontrack.extension.general.AutoPromotionProperty
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfigCascSetup
import net.nemerosa.ontrack.extension.github.ingestion.support.FilterHelper
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.EntityDataService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultConfigService(
    private val entityDataService: EntityDataService,
    private val configLoaderService: ConfigLoaderService,
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val propertyService: PropertyService,
    private val cascEntityService: CascEntityService,
) : ConfigService {

    override fun loadConfig(
        configuration: GitHubEngineConfiguration,
        repository: String,
        branch: String,
        path: String,
    ): IngestionConfig =
        configLoaderService.loadConfig(configuration, repository, branch, path)
            ?: IngestionConfig()

    override fun getOrLoadConfig(branch: Branch, path: String): IngestionConfig {
        return load(branch)
            ?: configLoaderService.loadConfig(branch, path)
                ?.apply { store(branch) } // Stores when loaded
            ?: IngestionConfig() // Default configuration
    }

    override fun loadAndSaveConfig(branch: Branch, path: String): IngestionConfig? {
        val config = configLoaderService.loadConfig(branch, path)
        return config?.apply {
            saveConfig(branch, this)
        }
    }

    private fun validations(branch: Branch, config: IngestionConfig) {
        config.setup.validations.forEach { validationConfig ->
            ingestionModelAccessService.setupValidationStamp(
                branch = branch,
                vsName = validationConfig.name,
                vsDescription = validationConfig.description,
                dataType = validationConfig.dataType?.type,
                dataTypeConfig = validationConfig.dataType?.config,
                image = validationConfig.image,
            )
        }
    }

    private fun autoPromotions(branch: Branch, config: IngestionConfig) {
        // Making sure all validations are created
        val validations = config.setup.promotions.flatMap { it.validations }.distinct().associateWith { validation ->
            ingestionModelAccessService.setupValidationStamp(branch, validation, null)
        }
        // Creating all promotions - first pass
        val promotions = config.setup.promotions.associate { plConfig ->
            plConfig.name to ingestionModelAccessService.setupPromotionLevel(
                branch,
                plConfig.name,
                plConfig.description,
                plConfig.image,
            )
        }
        // Configuring all promotions - second pass
        config.setup.promotions.forEach { plConfig ->
            val promotion = promotions[plConfig.name]
            if (promotion != null) {
                val existingAutoPromotionProperty: AutoPromotionProperty? =
                    propertyService.getProperty(promotion, AutoPromotionPropertyType::class.java).value
                val autoPromotionProperty = AutoPromotionProperty(
                    validationStamps = plConfig.validations.mapNotNull { validations[it] },
                    promotionLevels = plConfig.promotions.mapNotNull { promotions[it] },
                    include = plConfig.include ?: "",
                    exclude = plConfig.exclude ?: "",
                )
                if (existingAutoPromotionProperty == null || existingAutoPromotionProperty != autoPromotionProperty) {
                    propertyService.editProperty(
                        promotion,
                        AutoPromotionPropertyType::class.java,
                        autoPromotionProperty
                    )
                }
            }
        }
    }

    override fun saveConfig(branch: Branch, config: IngestionConfig) {
        // Storing the configuration
        config.store(branch)
        // Validations
        validations(branch, config)
        // Auto promotions
        autoPromotions(branch, config)
        // Applying Casc configuration nodes
        casc(branch.project, branch.name, config.setup.project)
        casc(branch, branch.name, config.setup.branch)
    }

    private fun casc(entity: ProjectEntity, branchName: String, cascConfig: IngestionConfigCascSetup) {
        if (!cascConfig.casc.isNull && FilterHelper.includes(branchName, cascConfig.includes, cascConfig.excludes)) {
            cascEntityService.apply(entity, cascConfig.casc)
        }
    }

    private fun IngestionConfig.store(ontrackBranch: Branch) {
        entityDataService.store(
            ontrackBranch,
            IngestionConfig::class.java.name,
            this,
        )
    }

    override fun removeConfig(branch: Branch) {
        entityDataService.delete(
            branch,
            IngestionConfig::class.java.name,
        )
    }

    override fun findConfig(branch: Branch): IngestionConfig? = load(branch)

    private fun load(ontrackBranch: Branch) = entityDataService.retrieve(
        ontrackBranch,
        IngestionConfig::class.java.name,
        IngestionConfig::class.java,
    )
}