package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.*
import java.util.Optional
import java.util.function.Function

/**
 * Standard filter query.
 */
interface CoreBuildFilterRepository {

    /**
     * Performs a standard filter at project level.
     */
    fun projectSearch(project: Project, form: BuildSearchForm, propertyTypeAccessor: (String) -> PropertyType<*>): List<Build>

    fun standardFilter(branch: Branch, data: StandardBuildFilterData, propertyTypeAccessor: (String) -> PropertyType<*>): List<Build>

    fun nameFilter(branch: Branch, fromBuild: String?, toBuild: String?, withPromotionLevel: String?, count: Int): List<Build>

    fun lastBuild(branch: Branch, sinceBuild: String?, withPromotionLevel: String?): Optional<Build>

    fun between(branch: Branch, from: String?, to: String?): List<Build>

}
