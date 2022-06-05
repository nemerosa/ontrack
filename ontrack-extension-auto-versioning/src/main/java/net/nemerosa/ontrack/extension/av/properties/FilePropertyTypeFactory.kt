package net.nemerosa.ontrack.extension.av.properties

interface FilePropertyTypeFactory {

    val defaultFilePropertyType: FilePropertyType

    fun getFilePropertyType(type: String): FilePropertyType?

}