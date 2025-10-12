package net.nemerosa.ontrack.extension.config.ci

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import net.nemerosa.ontrack.extension.config.ci.model.*
import net.nemerosa.ontrack.extension.config.ci.properties.PropertyAliasService
import net.nemerosa.ontrack.extension.config.ci.validations.ValidationDataTypeAliasService
import net.nemerosa.ontrack.extension.config.model.*
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.yaml.Yaml
import org.springframework.stereotype.Component

@Component
class CIConfigurationParserImpl(
    private val propertyService: PropertyService,
    private val validationDataTypeService: ValidationDataTypeService,
    private val propertyAliasService: PropertyAliasService,
    private val validationDataTypeAliasService: ValidationDataTypeAliasService,
) : CIConfigurationParser {

    override fun parseConfig(yaml: String): ConfigurationInput {
        // JSON parsing
        val documents = Yaml().read(yaml)
        return if (documents.size == 1) {
            val document = documents.first()
            val version = document.getTextField("version")
            if (version.isNullOrBlank()) {
                throw CIConfigVersionMissingException()
            } else if (version == "v1") {
                val ciConfigInput = document.parse<CIConfigInput>()
                convert(ciConfigInput)
            } else {
                throw CIConfigVersionUnsupportedException(version)
            }
        } else if (documents.size > 1) {
            throw CIConfigurationSeveralDocumentsException()
        } else {
            throw CIConfigurationNoDocumentException()
        }
    }

    private fun convert(ciConfigInput: CIConfigInput): ConfigurationInput {
        return ConfigurationInput(
            configuration = convertRootConfiguration(ciConfigInput.configuration),
        )
    }

    private fun convertRootConfiguration(ciConfigRoot: CIConfigRoot): RootConfiguration {
        return RootConfiguration(
            defaults = convertConfiguration(ciConfigRoot.defaults),
            custom = convertCustom(ciConfigRoot.custom),
        )
    }

    private fun convertCustom(custom: CICustom): Custom {
        return Custom(
            configs = custom.configs.map {
                convertCustomConfig(it)
            }
        )
    }

    private fun convertCustomConfig(customConfig: CICustomConfig): CustomConfig {
        return CustomConfig(
            conditions = customConfig.conditions.map { (name, data) ->
                ConditionConfig(name, data)
            },
            project = convertProject(customConfig.project),
            branch = convertBranch(customConfig.branch),
            build = convertBuild(customConfig.build),
        )
    }

    private fun convertConfiguration(ciConfig: CIConfig): Configuration {
        return Configuration(
            project = convertProject(ciConfig.project),
            branch = convertBranch(ciConfig.branch),
            build = convertBuild(ciConfig.build),
        )
    }

    private fun convertBuild(build: CIBuildConfig): BuildConfiguration {
        return BuildConfiguration(
            properties = convertProperties(build),
            autoVersioningCheck = build.autoVersioningCheck,
        )
    }

    private fun convertBranch(branch: CIBranchConfig): BranchConfiguration {
        return BranchConfiguration(
            properties = convertProperties(branch),
            validations = convertValidations(branch.validations),
            promotions = convertPromotions(branch.promotions),
            autoVersioning = branch.autoVersioning,
        )
    }

    private fun convertPromotions(promotions: Map<String, CIPromotionConfig>): List<PromotionLevelConfiguration> {
        return promotions.map { (name, config) ->
            PromotionLevelConfiguration(
                name = name,
                description = "",
                validations = config.validations,
                promotions = config.promotions,
            )
        }
    }

    private fun convertValidations(validations: Map<String, JsonNode>): List<ValidationStampConfiguration> {
        return validations.map { (name, config) ->
            convertValidation(name, config)
        }
    }

    private fun convertValidation(
        name: String,
        config: JsonNode
    ): ValidationStampConfiguration {
        return ValidationStampConfiguration(
            name = name,
            description = config.getTextField("description") ?: "",
            validationStampDataConfiguration = convertValidationData(name, config),
        )
    }

    private fun convertValidationData(name: String, config: JsonNode): ValidationStampDataConfiguration? {
        return if (config is ObjectNode) {
            val names = config.fieldNames().asSequence().toList().toMutableList()
            names.remove("description")
            if (names.isEmpty()) {
                null
            } else if (names.size == 1) {
                val type = names.first()
                val typeConfig = config.get(type)
                convertValidationDataType(type, typeConfig)
            } else {
                throw ValidationStampDataConfigurationWrongFormatException(name)
            }
        } else {
            null
        }
    }

    private fun convertValidationDataType(
        type: String,
        typeConfig: JsonNode
    ): ValidationStampDataConfiguration {
        val validationDataTypeAlias = validationDataTypeAliasService.findValidationDataTypeAlias(type)
        return if (validationDataTypeAlias != null) {
            convertActualValidationDataType(
                type = validationDataTypeAlias.type,
                data = validationDataTypeAlias.parseConfig(typeConfig),
            )
        } else {
            convertActualValidationDataType(
                type = type,
                data = typeConfig,
            )
        }
    }

    private fun convertActualValidationDataType(
        type: String,
        data: JsonNode
    ): ValidationStampDataConfiguration {
        val config = validationDataTypeService.validateValidationDataTypeConfig<Any>(type, data)
            ?: throw ValidationStampDataConfigurationTypeNotFoundException(type)
        return ValidationStampDataConfiguration(
            type = config.descriptor.id,
            data = config.config.asJson(),
        )
    }

    private fun convertProject(project: CIProjectConfig): ProjectConfiguration {
        return ProjectConfiguration(
            properties = convertProperties(project),
            projectName = project.name,
            scmConfig = project.scmConfig,
            issueServiceIdentifier = project.issueServiceIdentifier,
        )
    }

    private fun convertProperties(ciPropertiesConfig: CIPropertiesConfig): List<PropertyConfiguration> {
        return ciPropertiesConfig.properties.map { (type, data) ->
            convertProperty(type, data)
        }
    }

    private fun convertProperty(
        type: String,
        data: JsonNode
    ): PropertyConfiguration {
        val propertyAlias = propertyAliasService.findPropertyAlias(type)
        return if (propertyAlias != null) {
            convertActualProperty(
                type = propertyAlias.type,
                data = propertyAlias.parseConfig(data),
            )
        } else {
            convertActualProperty(
                type = type,
                data = data,
            )
        }
    }

    private fun convertActualProperty(
        type: String,
        data: JsonNode
    ): PropertyConfiguration {
        val propertyType = propertyService.getPropertyTypeByName<Any>(type)
        return convertPropertyType(propertyType, data)
    }

    private fun <T> convertPropertyType(
        propertyType: PropertyType<T>,
        data: JsonNode
    ): PropertyConfiguration {
        val value = propertyType.fromClient(data)
        return PropertyConfiguration(
            type = propertyType.typeName,
            data = propertyType.forStorage(value)
        )
    }

}