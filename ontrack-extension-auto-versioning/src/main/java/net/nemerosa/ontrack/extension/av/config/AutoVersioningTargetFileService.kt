package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.properties.FilePropertyType

interface AutoVersioningTargetFileService {

    fun readVersion(path: AutoVersioningSourceConfigPath, lines: List<String>): String?

    fun getFilePropertyType(path: AutoVersioningSourceConfigPath): FilePropertyType

}