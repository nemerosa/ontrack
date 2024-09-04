package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.properties.FilePropertyType
import net.nemerosa.ontrack.extension.av.properties.FilePropertyTypeFactory
import net.nemerosa.ontrack.extension.av.properties.RegexFilePropertyType
import org.springframework.stereotype.Component

@Component
class AutoVersioningTargetFileServiceImpl(
    private val filePropertyTypeFactory: FilePropertyTypeFactory,
) : AutoVersioningTargetFileService {

    override fun readVersion(path: AutoVersioningSourceConfigPath, lines: List<String>): String? {
        val filePropertyType = getFilePropertyType(path)
        val rawValue = filePropertyType.readProperty(lines, path.property)
        val propertyRegex = path.propertyRegex
        return if (rawValue == null || propertyRegex.isNullOrBlank()) {
            rawValue
        } else {
            propertyRegex.toRegex().matchEntire(rawValue)?.groups?.get(1)?.value
        }
    }


    override fun getFilePropertyType(path: AutoVersioningSourceConfigPath): FilePropertyType =
        if (path.property.isNullOrBlank()) {
            path.regex
                ?.let { RegexFilePropertyType(it) }
                ?: error("Regex is not defined")
        } else {
            path.propertyType
                ?.let {
                    filePropertyTypeFactory.getFilePropertyType(it)
                        ?: error("Cannot find any file property type for ${path.propertyType}")
                }
                ?: filePropertyTypeFactory.defaultFilePropertyType
        }
}