package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.isNotBlank
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import java.lang.String.format
import java.util.*
import javax.sql.DataSource
import kotlin.math.max
import kotlin.math.min

@Repository
class CoreBuildFilterJdbcRepository(
    dataSource: DataSource,
    private val structureRepository: StructureRepository,
    private val ontrackConfigProperties: OntrackConfigProperties,
) : AbstractJdbcRepository(dataSource), CoreBuildFilterRepository {

    override fun projectSearch(
        project: Project,
        form: BuildSearchForm,
        propertyTypeAccessor: (String) -> PropertyType<*>,
    ): List<Build> {
        // Query root
        val tables = StringBuilder("""
            SELECT DISTINCT(B.ID) FROM BUILDS B
            INNER JOIN BRANCHES BB ON BB.ID = B.BRANCHID
        """)
        // Criterias
        val criteria = StringBuilder(" WHERE BB.PROJECTID = :project")
        // Parameters
        val params = MapSqlParameterSource("project", project.id())

        // branchName
        if (!form.branchName.isNullOrBlank()) {
            try {
                form.branchName.toRegex()
            } catch (_: Exception) {
                // We ignore invalid regexes
                return emptyList()
            }
            criteria.append(" AND BB.NAME ~ :branchName")
            params.addValue("branchName", form.branchName)
        }

        // buildName
        if (!form.buildName.isNullOrBlank()) {
            if (form.isBuildExactMatch) {
                criteria.append(" AND B.NAME = :buildName")
            } else {
                try {
                    form.buildName.toRegex()
                } catch (_: Exception) {
                    // We ignore invalid regexes
                    return emptyList()
                }
                criteria.append(" AND B.NAME ~ :buildName")
            }
            params.addValue("buildName", form.buildName)
        }

        // promotionName
        if (!form.promotionName.isNullOrBlank()) {
            tables.append(""" 
                INNER JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID
                INNER JOIN PROMOTION_LEVELS PL ON PL.ID = PR.PROMOTIONLEVELID
            """)
            criteria.append(" AND PL.NAME = :promotionName")
            params.addValue("promotionName", form.promotionName)
        }

        // validationStampName
        if (!form.validationStampName.isNullOrBlank()) {
            tables.append(""" 
                INNER JOIN VALIDATION_RUNS VR ON VR.BUILDID = B.ID
                INNER JOIN VALIDATION_STAMPS VS ON VS.ID = VR.VALIDATIONSTAMPID
            """)
            criteria.append(" AND VS.NAME = :validationStampName")
            params.addValue("validationStampName", form.validationStampName)
        }

        // property
        if (!form.property.isNullOrBlank()) {
            tables.append("""
                INNER JOIN PROPERTIES PP ON PP.BUILD = B.ID 
            """)
            criteria.append(" AND PP.TYPE = :property")
            params.addValue("property", form.property)
            if (!form.propertyValue.isNullOrBlank()) {
                // Gets the property type
                val propertyType = propertyTypeAccessor(form.property)
                // Gets the search arguments
                val searchArguments = propertyType.getSearchArguments(form.propertyValue)
                // If defined use them
                if (searchArguments != null && searchArguments.isDefined) {
                    PropertyJdbcRepository.prepareQueryForPropertyValue(
                        searchArguments,
                        tables,
                        criteria,
                        params
                    )
                } else {
                    // No match possible - not performing the request
                    return emptyList()
                }
            }
        }
        val linkedFrom: String? = form.linkedFrom
        if (!linkedFrom.isNullOrBlank()) {
            tables.append(
                " LEFT JOIN BUILD_LINKS BLFROM ON BLFROM.TARGETBUILDID = B.ID" +
                        " LEFT JOIN BUILDS BDFROM ON BDFROM.ID = BLFROM.BUILDID" +
                        " LEFT JOIN BRANCHES BRFROM ON BRFROM.ID = BDFROM.BRANCHID" +
                        " LEFT JOIN PROJECTS PJFROM ON PJFROM.ID = BRFROM.PROJECTID"
            )
            val projectFrom = linkedFrom.substringBefore(":")
            criteria.append(" AND PJFROM.NAME = :fromProject")
            params.addValue("fromProject", projectFrom)
            val buildPattern = linkedFrom.substringAfter(":", "")
            if (buildPattern.isNotBlank()) {
                if ("*" in buildPattern) {
                    criteria.append(" AND BDFROM.NAME LIKE :buildFrom")
                    params.addValue("buildFrom", buildPattern.replace("*", "%"))
                } else {
                    criteria.append(" AND BDFROM.NAME = :buildFrom")
                    params.addValue("buildFrom", buildPattern)
                }
            }
        }

        // linkedTo
        val linkedTo: String? = form.linkedTo
        if (!linkedTo.isNullOrBlank()) {
            tables.append(
                " LEFT JOIN BUILD_LINKS BLTO ON BLTO.BUILDID = B.ID" +
                        " LEFT JOIN BUILDS BDTO ON BDTO.ID = BLTO.TARGETBUILDID" +
                        " LEFT JOIN BRANCHES BRTO ON BRTO.ID = BDTO.BRANCHID" +
                        " LEFT JOIN PROJECTS PJTO ON PJTO.ID = BRTO.PROJECTID"
            )
            val projectTo = linkedTo.substringBefore(":")
            criteria.append(" AND PJTO.NAME = :toProject")
            params.addValue("toProject", projectTo)
            val buildPattern = linkedTo.substringAfter(":", "")
            if (buildPattern.isNotBlank()) {
                if ("*" in buildPattern) {
                    criteria.append(" AND BDTO.NAME LIKE :buildTo")
                    params.addValue("buildTo", buildPattern.replace("*", "%"))
                } else {
                    criteria.append(" AND BDTO.NAME = :buildTo")
                    params.addValue("buildTo", buildPattern)
                }
            }
        }

        // Final SQL
        val count = min(form.maximumCount, ontrackConfigProperties.buildFilterCountMax)
        val sql = "$tables $criteria ORDER BY B.ID DESC LIMIT $count"

        // Running the query
        return loadBuilds(sql, params)
    }

    /**
     * The fat join query (dreaming of Neo4J) defines the following columns:
     *
     * ```
     * B (BUILDS)
     * PR (PROMOTION_RUNS)
     * PL (PROMOTION_LEVELS)
     * S (last validation run status)
     * VALIDATIONSTAMPID
     * VALIDATIONRUNSTATUSID
     * PP (PROPERTIES)
     * BDFROM (builds linked from)
     * BRFROM (branches linked from)
     * PJFROM (projects linked from)
     * PLFROM (promotions linked from)
     * BDTO (builds linked to)
     * BRTO (branches linked to)
     * PJTO (projects linked to)
     * PLTO (promotions linked to)
     * ```
     */
    override fun standardFilter(
        branch: Branch,
        data: StandardBuildFilterData,
        propertyTypeAccessor: (String) -> PropertyType<*>,
    ): List<Build> {
        // Query root
        val tables = StringBuilder("SELECT DISTINCT(B.ID) FROM BUILDS B ")
        // Criterias
        val criteria = StringBuilder(" WHERE B.BRANCHID = :branch")

        // Parameters
        val params = MapSqlParameterSource("branch", branch.id())
        var sinceBuildId: Int? = null

        // sincePromotionLevel
        val sincePromotionLevel = data.sincePromotionLevel
        if (isNotBlank(sincePromotionLevel)) {
            // Gets the promotion level ID
            val promotionLevelId = structureRepository
                .getPromotionLevelByName(branch, sincePromotionLevel)
                .map { it.id() }
                .orElse(-1)
            // Gets the last build having this promotion level
            val id = findLastBuildWithPromotionLevel(promotionLevelId)
            if (id != null) {
                sinceBuildId = id
            }
        }

        // withPromotionLevel
        val withPromotionLevel = data.withPromotionLevel
        if (isNotBlank(withPromotionLevel)) {
            tables.append(
                " LEFT JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID" + " LEFT JOIN PROMOTION_LEVELS PL ON PL.ID = PR.PROMOTIONLEVELID")
            criteria.append(" AND PL.NAME = :withPromotionLevel")
            params.addValue("withPromotionLevel", withPromotionLevel)
        }

        // afterDate
        val afterDate = data.afterDate
        if (afterDate != null) {
            criteria.append(" AND B.CREATION >= :afterDate")
            params.addValue("afterDate", dateTimeForDB(afterDate.atTime(0, 0)))
        }

        // beforeDate
        val beforeDate = data.beforeDate
        if (beforeDate != null) {
            criteria.append(" AND B.CREATION <= :beforeDate")
            params.addValue("beforeDate", dateTimeForDB(beforeDate.atTime(23, 59, 59)))
        }

        // sinceValidationStamp
        val sinceValidationStamp = data.sinceValidationStamp
        if (isNotBlank(sinceValidationStamp)) {
            // Gets the validation stamp ID
            val validationStampId = getValidationStampId(branch, sinceValidationStamp)!!
            // Gets the last build having this validation stamp and the validation status
            val id = findLastBuildWithValidationStamp(validationStampId, data.sinceValidationStampStatus)
            if (id != null) {
                sinceBuildId = if (sinceBuildId == null) {
                    id
                } else {
                    max(sinceBuildId, id)
                }
            }
        }

        // withValidationStamp
        val withValidationStamp = data.withValidationStamp
        if (isNotBlank(withValidationStamp)) {
            tables.append(
                "  LEFT JOIN (" +
                        " SELECT R.BUILDID,  R.VALIDATIONSTAMPID, VRS.VALIDATIONRUNSTATUSID " +
                        " FROM VALIDATION_RUNS R" +
                        " INNER JOIN VALIDATION_RUN_STATUSES VRS ON VRS.ID = (SELECT ID FROM VALIDATION_RUN_STATUSES WHERE VALIDATIONRUNID = R.ID ORDER BY ID DESC LIMIT 1)" +
                        " AND R.ID = (SELECT MAX(ID) FROM VALIDATION_RUNS WHERE BUILDID = R.BUILDID AND VALIDATIONSTAMPID = R.VALIDATIONSTAMPID)" +
                        " ) S ON S.BUILDID = B.ID"
            )
            // Gets the validation stamp ID
            val validationStampId = getValidationStampId(branch, withValidationStamp)!!
            criteria.append(" AND (S.VALIDATIONSTAMPID = :validationStampId")
            params.addValue("validationStampId", validationStampId)
            // withValidationStampStatus
            val withValidationStampStatus = data.withValidationStampStatus
            if (isNotBlank(withValidationStampStatus)) {
                criteria.append(" AND S.VALIDATIONRUNSTATUSID = :withValidationStampStatus")
                params.addValue("withValidationStampStatus", withValidationStampStatus)
            }
            criteria.append(")")
        }

        // withProperty
        val withProperty = data.withProperty
        if (isNotBlank(withProperty)) {
            tables.append(" LEFT JOIN PROPERTIES PP ON PP.BUILD = B.ID")
            criteria.append(" AND PP.TYPE = :withProperty")
            params.addValue("withProperty", withProperty)
            // withPropertyValue
            val withPropertyValue = data.withPropertyValue
            if (isNotBlank(withPropertyValue)) {
                // Gets the property type
                val propertyType = propertyTypeAccessor(withProperty)
                // Gets the search arguments
                val searchArguments = propertyType.getSearchArguments(withPropertyValue)
                // If defined use them
                if (searchArguments != null && searchArguments.isDefined) {
                    PropertyJdbcRepository.prepareQueryForPropertyValue(
                        searchArguments,
                        tables,
                        criteria,
                        params
                    )
                } else {
                    // No match
                    return emptyList()
                }
            }
        }

        // sinceProperty
        val sinceProperty = data.sinceProperty
        if (isNotBlank(sinceProperty)) {
            val sincePropertyValue = data.sincePropertyValue
            val id = findLastBuildWithPropertyValue(branch, sinceProperty, sincePropertyValue, propertyTypeAccessor)
            if (id != null) {
                sinceBuildId = if (sinceBuildId == null) {
                    id
                } else {
                    max(sinceBuildId, id)
                }
            }
        }

        // linkedFrom
        val linkedFrom = data.linkedFrom
        if (isNotBlank(linkedFrom)) {
            tables.append(
                " LEFT JOIN BUILD_LINKS BLFROM ON BLFROM.TARGETBUILDID = B.ID" +
                        " LEFT JOIN BUILDS BDFROM ON BDFROM.ID = BLFROM.BUILDID" +
                        " LEFT JOIN BRANCHES BRFROM ON BRFROM.ID = BDFROM.BRANCHID" +
                        " LEFT JOIN PROJECTS PJFROM ON PJFROM.ID = BRFROM.PROJECTID"
            )
            val project = StringUtils.substringBefore(linkedFrom, ":")
            criteria.append(" AND PJFROM.NAME = :fromProject")
            params.addValue("fromProject", project)
            val buildPattern = StringUtils.substringAfter(linkedFrom, ":")
            if (isNotBlank(buildPattern)) {
                if (StringUtils.contains(buildPattern, "*")) {
                    criteria.append(" AND BDFROM.NAME LIKE :buildFrom")
                    params.addValue("buildFrom", StringUtils.replace(buildPattern, "*", "%"))
                } else {
                    criteria.append(" AND BDFROM.NAME = :buildFrom")
                    params.addValue("buildFrom", buildPattern)
                }
            }
            // linkedFromPromotion
            val linkedFromPromotion = data.linkedFromPromotion
            if (isNotBlank(linkedFromPromotion)) {
                tables.append(
                    " LEFT JOIN PROMOTION_RUNS PRFROM ON PRFROM.BUILDID = BDFROM.ID" + " LEFT JOIN PROMOTION_LEVELS PLFROM ON PLFROM.ID = PRFROM.PROMOTIONLEVELID"
                )
                criteria.append(" AND PLFROM.NAME = :linkedFromPromotion")
                params.addValue("linkedFromPromotion", linkedFromPromotion)
            }
        }

        // linkedTo
        val linkedTo = data.linkedTo
        if (isNotBlank(linkedTo)) {
            tables.append(
                " LEFT JOIN BUILD_LINKS BLTO ON BLTO.BUILDID = B.ID" +
                        " LEFT JOIN BUILDS BDTO ON BDTO.ID = BLTO.TARGETBUILDID" +
                        " LEFT JOIN BRANCHES BRTO ON BRTO.ID = BDTO.BRANCHID" +
                        " LEFT JOIN PROJECTS PJTO ON PJTO.ID = BRTO.PROJECTID"
            )
            val project = StringUtils.substringBefore(linkedTo, ":")
            criteria.append(" AND PJTO.NAME = :toProject")
            params.addValue("toProject", project)
            val buildPattern = StringUtils.substringAfter(linkedTo, ":")
            if (isNotBlank(buildPattern)) {
                if (StringUtils.contains(buildPattern, "*")) {
                    criteria.append(" AND BDTO.NAME LIKE :buildTo")
                    params.addValue("buildTo", StringUtils.replace(buildPattern, "*", "%"))
                } else {
                    criteria.append(" AND BDTO.NAME = :buildTo")
                    params.addValue("buildTo", buildPattern)
                }
            }
            // linkedToPromotion
            val linkedToPromotion = data.linkedToPromotion
            if (isNotBlank(linkedToPromotion)) {
                tables.append(
                    " LEFT JOIN PROMOTION_RUNS PRTO ON PRTO.BUILDID = BDTO.ID" + " LEFT JOIN PROMOTION_LEVELS PLTO ON PLTO.ID = PRTO.PROMOTIONLEVELID"
                )
                criteria.append(" AND PLTO.NAME = :linkedToPromotion")
                params.addValue("linkedToPromotion", linkedToPromotion)
            }
        }

        // Since build?
        if (sinceBuildId != null) {
            criteria.append(" AND B.ID >= :sinceBuildId")
            params.addValue("sinceBuildId", sinceBuildId)
        }

        // Final SQL
        val sql = format(
            "%s %s ORDER BY B.ID DESC LIMIT :count",
            tables,
            criteria)
        params.addValue("count", min(data.count, ontrackConfigProperties.buildFilterCountMax))

        // Running the query
        return loadBuilds(sql, params)
    }

    private fun loadBuilds(sql: String, params: MapSqlParameterSource): List<Build> {
        return namedParameterJdbcTemplate!!
            .queryForList(
                sql,
                params,
                Int::class.java
            )
            .map { id -> structureRepository.getBuild(ID.of(id)) }
    }

    override fun nameFilter(
        branch: Branch,
        fromBuild: String?,
        toBuild: String?,
        withPromotionLevel: String?,
        count: Int,
    ): List<Build> {
        // Query root
        val sql = StringBuilder("SELECT DISTINCT(B.ID) FROM BUILDS B" +
                "                LEFT JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID" +
                "                LEFT JOIN PROMOTION_LEVELS PL ON PL.ID = PR.PROMOTIONLEVELID" +
                // Branch criteria
                "                WHERE B.BRANCHID = :branch")

        // Parameters
        val params = MapSqlParameterSource("branch", branch.id())

        // From build
        val fromBuildId = lastBuild(branch, fromBuild, null)
            .map { it.id() }
        if (!fromBuildId.isPresent) {
            return emptyList()
        }
        sql.append(" AND B.ID >= :fromBuildId")
        params.addValue("fromBuildId", fromBuildId.get())

        // To build
        if (isNotBlank(toBuild)) {
            val toBuildId = lastBuild(branch, toBuild, null).map { it.id() }
            if (toBuildId.isPresent) {
                sql.append(" AND B.ID <= :toBuildId")
                params.addValue("toBuildId", toBuildId.get())
            }
        }

        // With promotion
        if (isNotBlank(withPromotionLevel)) {
            sql.append(" AND PL.NAME = :withPromotionLevel")
            params.addValue("withPromotionLevel", withPromotionLevel)
        }

        // Ordering
        sql.append(" ORDER BY B.ID DESC")
        // Limit
        sql.append(" LIMIT :count")
        params.addValue("count", min(count, ontrackConfigProperties.buildFilterCountMax))

        // Running the query
        return loadBuilds(sql.toString(), params)
    }

    override fun lastBuild(branch: Branch, sinceBuild: String?, withPromotionLevel: String?): Optional<Build> {
        // Query root
        val sql = StringBuilder("SELECT DISTINCT(B.ID) FROM BUILDS B" +
                "                LEFT JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID" +
                "                LEFT JOIN PROMOTION_LEVELS PL ON PL.ID = PR.PROMOTIONLEVELID" +
                // Branch criteria
                "                WHERE B.BRANCHID = :branch")

        // Parameters
        val params = MapSqlParameterSource("branch", branch.id())

        // Since build
        if (StringUtils.contains(sinceBuild, "*")) {
            sql.append(" AND B.NAME LIKE :buildName")
            params.addValue("buildName", StringUtils.replace(sinceBuild, "*", "%"))
        } else {
            sql.append(" AND B.NAME = :buildName")
            params.addValue("buildName", sinceBuild)
        }

        // With promotion
        if (isNotBlank(withPromotionLevel)) {
            sql.append(" AND PL.NAME = :withPromotionLevel")
            params.addValue("withPromotionLevel", withPromotionLevel)
        }

        // Ordering
        sql.append(" ORDER BY B.ID DESC")
        // Limit
        sql.append(" LIMIT 1")

        // Running the query
        return loadBuilds(sql.toString(), params)
            .stream()
            .findFirst()
    }

    override fun between(branch: Branch, from: String?, to: String?): List<Build> {
        val sql = StringBuilder(
            "SELECT ID FROM BUILDS WHERE " + "BRANCHID = :branchId "
        )
        val params = params("branchId", branch.id())
        var fromId: Int?
        var toId: Int? = null

        try {

            // From build
            fromId = structureRepository.getBuildByName(branch.project.name, branch.name, from)
                .orElseThrow { BuildNotFoundException(branch.project.name, branch.name, from) }
                .id()

            // To build
            if (isNotBlank(to)) {
                toId = structureRepository.getBuildByName(branch.project.name, branch.name, to)
                    .orElseThrow { BuildNotFoundException(branch.project.name, branch.name, to) }
                    .id()
            }

        } catch (ex: BuildNotFoundException) {
            // If one of the boundaries is not found, returns an empty list
            return emptyList()
        }

        if (toId != null) {
            if (toId < fromId) {
                val i = toId
                toId = fromId
                fromId = i
            }
        }

        sql.append(" AND ID >= :fromId")
        params.addValue("fromId", fromId)
        if (toId != null) {
            sql.append(" AND ID <= :toId")
            params.addValue("toId", toId)
        }

        // Ordering
        sql.append(" ORDER BY ID DESC")

        // Query
        return loadBuilds(sql.toString(), params)
    }

    private fun findLastBuildWithPropertyValue(
        branch: Branch,
        propertyTypeName: String,
        propertyValue: String?,
        propertyTypeAccessor: (String) -> PropertyType<*>,
    ): Int? {
        // SQL
        val tables = StringBuilder("SELECT B.ID " +
                "FROM BUILDS B " +
                "LEFT JOIN PROPERTIES PP ON PP.BUILD = B.ID "
        )
        val criteria = StringBuilder(
            "WHERE B.BRANCHID = :branchId " + "AND PP.TYPE = :propertyType "
        )
        val params = params("branchId", branch.id())
            .addValue("propertyType", propertyTypeName)

        if (isNotBlank(propertyValue)) {
            // Gets the property type
            val propertyType = propertyTypeAccessor(propertyTypeName)
            // Gets the search arguments for a property value
            val searchArguments = propertyType.getSearchArguments(propertyValue)
            // If defined use them
            if (searchArguments != null && searchArguments.isDefined) {
                PropertyJdbcRepository.prepareQueryForPropertyValue(
                    searchArguments,
                    tables,
                    criteria,
                    params
                )
            } else {
                // No match
                return null
            }
        }

        // Build ID
        val sql = tables.toString() + " " +
                criteria + "" +
                " ORDER BY B.ID DESC LIMIT 1"

        // Runs the query
        return getFirstItem(
            sql,
            params,
            Int::class.java
        )
    }

    private fun getValidationStampId(branch: Branch, validationStampName: String): Int? {
        return structureRepository
            .getValidationStampByName(branch, validationStampName)
            .map { it.id() }
            .orElse(-1)
    }

    private fun findLastBuildWithValidationStamp(validationStampId: Int, status: String?): Int? {
        val sql = StringBuilder(
            "SELECT VR.BUILDID FROM VALIDATION_RUN_STATUSES VRS\n" +
                    "INNER JOIN VALIDATION_RUNS VR ON VR.ID = VRS.VALIDATIONRUNID\n" +
                    "WHERE VR.VALIDATIONSTAMPID = :validationStampId\n"
        )
        // Parameters
        val params = params("validationStampId", validationStampId)
        // Status criteria
        if (isNotBlank(status)) {
            sql.append("AND VRS.VALIDATIONRUNSTATUSID = :status\n")
            params.addValue("status", status)
        }
        // Order & limit
        sql.append("ORDER BY VR.BUILDID DESC LIMIT 1\n")
        // Build ID
        return getFirstItem(
            sql.toString(),
            params,
            Int::class.java
        )
    }

    private fun findLastBuildWithPromotionLevel(promotionLevelId: Int): Int? {
        return getFirstItem(
            "SELECT BUILDID FROM PROMOTION_RUNS WHERE PROMOTIONLEVELID = :promotionLevelId ORDER BY BUILDID DESC LIMIT 1",
            params("promotionLevelId", promotionLevelId),
            Int::class.java
        )
    }
}
