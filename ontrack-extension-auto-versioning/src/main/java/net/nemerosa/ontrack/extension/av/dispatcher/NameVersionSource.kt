package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import org.springframework.stereotype.Component

@Component
class NameVersionSource : VersionSource {

    override val id: String = "name"

    override fun getVersion(build: Build, config: String?): String = build.name
}