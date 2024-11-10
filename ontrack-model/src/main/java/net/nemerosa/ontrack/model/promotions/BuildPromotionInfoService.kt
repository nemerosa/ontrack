package net.nemerosa.ontrack.model.promotions

import net.nemerosa.ontrack.model.structure.Build

/**
 * Service used to collect information about the promotions of a build.
 */
interface BuildPromotionInfoService {

    fun getBuildPromotionInfo(build: Build): BuildPromotionInfo

}