package net.nemerosa.ontrack.extension.av.properties

import org.springframework.stereotype.Component

@Component
class FilePropertyTypeFactoryImpl(
    types: List<FilePropertyType>,
) : FilePropertyTypeFactory {

    private val index = types.associateBy { it.id }

    override val defaultFilePropertyType: FilePropertyType =
        index["properties"] ?: error("`properties` file property type is required")

    override fun getFilePropertyType(type: String): FilePropertyType? =
        index[type]

}