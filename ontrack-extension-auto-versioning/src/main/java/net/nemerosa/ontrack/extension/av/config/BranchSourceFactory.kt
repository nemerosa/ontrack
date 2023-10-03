package net.nemerosa.ontrack.extension.av.config

/**
 * Management of [BranchSource] services.
 */
interface BranchSourceFactory {

    /**
     *
     */
    fun getBranchSource(id: String): BranchSource

}