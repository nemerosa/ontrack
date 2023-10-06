package net.nemerosa.ontrack.extension.av.dispatcher

import org.springframework.stereotype.Component

@Component
class VersionSourceFactoryImpl(
    versionSources: List<VersionSource>,
) : VersionSourceFactory {

    private val index by lazy {
        versionSources.associateBy { it.id }
    }

    override fun getVersionSource(id: String): VersionSource =
        index[id] ?: throw VersionSourceNotFoundException(id)

}
