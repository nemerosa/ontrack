package net.nemerosa.ontrack.extension.av.dispatcher

/**
 * Getting version sources using their [id][VersionSource.id].
 */
interface VersionSourceFactory {

    /**
     * Gets a version source using its ID
     *
     * @param id [id][VersionSource.id] to look for
     * @return Version source to use
     * @throws VersionSourceNotFoundException When the version source cannot be found
     */
    fun getVersionSource(id: String): VersionSource

}