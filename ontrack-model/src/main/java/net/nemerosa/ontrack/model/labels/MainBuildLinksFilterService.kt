package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.structure.Build

/**
 * Filters a build according to a list of project labels.
 */
interface MainBuildLinksFilterService {

    fun isMainBuidLink(target: Build, labels: List<String>): Boolean

}