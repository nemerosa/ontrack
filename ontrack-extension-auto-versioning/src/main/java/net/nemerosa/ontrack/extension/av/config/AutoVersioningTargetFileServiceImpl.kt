package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.properties.FilePropertyType
import net.nemerosa.ontrack.extension.av.properties.FilePropertyTypeFactory
import net.nemerosa.ontrack.extension.av.properties.RegexFilePropertyType
import org.springframework.stereotype.Component

@Component
class AutoVersioningTargetFileServiceImpl(
    private val filePropertyTypeFactory: FilePropertyTypeFactory,
) : AutoVersioningTargetFileService {

    override fun readVersion(config: AutoVersioningTargetConfig, lines: List<String>): String? {
        val filePropertyType = getFilePropertyType(config)
        val rawValue = filePropertyType.readProperty(lines, config.targetProperty)
        val propertyRegex = config.targetPropertyRegex
        return if (rawValue == null || propertyRegex.isNullOrBlank()) {
            rawValue
        } else {
            propertyRegex.toRegex().matchEntire(rawValue)?.groups?.get(1)?.value
        }
    }


    override fun getFilePropertyType(config: AutoVersioningTargetConfig): FilePropertyType =
        if (config.targetProperty.isNullOrBlank()) {
            config.targetRegex
                ?.let { RegexFilePropertyType(it) }
                ?: error("Regex is not defined")
        } else {
            config.targetPropertyType
                ?.let {
                    filePropertyTypeFactory.getFilePropertyType(it)
                        ?: error("Cannot find any file property type for ${config.targetPropertyType}")
                }
                ?: filePropertyTypeFactory.defaultFilePropertyType
        }
}