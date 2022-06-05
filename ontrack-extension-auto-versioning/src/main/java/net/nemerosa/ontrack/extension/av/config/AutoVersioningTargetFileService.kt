package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.properties.FilePropertyType

interface AutoVersioningTargetFileService {

    fun readVersion(config: AutoVersioningTargetConfig, lines: List<String>): String?

    fun getFilePropertyType(config: AutoVersioningTargetConfig): FilePropertyType

}