package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyType
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData
import java.util.Optional
import java.util.function.Function

/**
 * Standard filter query.
 */
interface CoreBuildFilterRepository {

    fun standardFilter(branch: Branch, data: StandardBuildFilterData, propertyTypeAccessor: (String) -> PropertyType<*>): List<Build>

    fun nameFilter(branch: Branch, fromBuild: String?, toBuild: String?, withPromotionLevel: String?, count: Int): List<Build>

    fun lastBuild(branch: Branch, sinceBuild: String?, withPromotionLevel: String?): Optional<Build>

    fun between(branch: Branch, from: String?, to: String?): List<Build>

}
