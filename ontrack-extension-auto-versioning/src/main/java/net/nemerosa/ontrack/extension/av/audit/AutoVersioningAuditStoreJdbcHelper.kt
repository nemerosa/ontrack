package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource

@Repository
@Transactional(readOnly = true)
class AutoVersioningAuditStoreJdbcHelper(
    dataSource: DataSource,
    private val structureService: StructureService,
) : AbstractJdbcRepository(dataSource), AutoVersioningAuditStoreHelper {

    override fun countByState(state: AutoVersioningAuditState): Int =
        namedParameterJdbcTemplate!!.queryForObject(
            """
               SELECT COUNT(ID)
               FROM ENTITY_DATA_STORE
               WHERE CATEGORY = '${AutoVersioningAuditStoreConstants.STORE_CATEGORY}'
               AND JSON::JSONB->>'mostRecentState' = :state 
            """,
            params("state", state.name),
            Int::class.java
        ) ?: 0

    override fun auditVersioningEntriesCount(filter: AutoVersioningAuditQueryFilter): Int {
        val params = mutableMapOf<String, Any>()
        val query = auditVersioningEntriesQuery(filter, params, select = "SELECT COUNT(S.ID)", limits = false)

        // Runs the query
        return namedParameterJdbcTemplate!!.queryForObject(query, params, Int::class.java) ?: 0
    }

    override fun auditVersioningEntries(filter: AutoVersioningAuditQueryFilter): MutableList<AutoVersioningAuditEntry> {
        val params = mutableMapOf<String, Any>()
        val query = auditVersioningEntriesQuery(filter, params)

        // Runs the query
        return namedParameterJdbcTemplate!!.query(query, params) { rs, _ ->
            val uuid: String = rs.getString("NAME")
            val json: JsonNode = readJson(rs, "JSON")
            val data = json.parse<AutoVersioningAuditStoreData>()
            // Loads the entity (we always expect a branch)
            val branchId: Int = rs.getInt("BRANCH")
            val branch = structureService.getBranch(ID.of(branchId))
            // Conversion
            AutoVersioningAuditEntry(
                order = AutoVersioningOrder(
                    uuid = uuid,
                    branch = branch,
                    sourceProject = data.sourceProject,
                    targetPaths = data.targetPaths,
                    targetRegex = data.targetRegex,
                    targetProperty = data.targetProperty,
                    targetPropertyRegex = data.targetPropertyRegex,
                    targetPropertyType = data.targetPropertyType,
                    targetVersion = data.targetVersion,
                    autoApproval = data.autoApproval,
                    upgradeBranchPattern = data.upgradeBranchPattern,
                    postProcessing = data.postProcessing,
                    postProcessingConfig = data.postProcessingConfig,
                    validationStamp = data.validationStamp,
                    autoApprovalMode = data.autoApprovalMode,
                ),
                audit = data.states,
                routing = data.routing,
                queue = data.queue,
            )
        }
    }

    private fun auditVersioningEntriesQuery(
        filter: AutoVersioningAuditQueryFilter,
        params: MutableMap<String, Any>,
        select: String = "SELECT * ",
        limits: Boolean = true,
    ): String {
        // Base query
        var query = """
            WHERE S.CATEGORY = '${AutoVersioningAuditStoreConstants.STORE_CATEGORY}'
        """
        var joins = ""

        // Filter on uuid
        filter.uuid?.takeIf { it.isNotBlank() }?.let {
            query += " AND S.NAME = :uuid"
            params += "uuid" to it
        }
        // Combine the JSON filters into one
        val jsonQueries = mutableListOf<String>()
        // Filter on state
        filter.state?.let {
            jsonQueries += "S.json::jsonb->>'mostRecentState' = :state"
            params += "state" to it.name
        }
        // Filter on state(s)
        if (filter.states != null && filter.states.isNotEmpty()) {
            val states = filter.states.joinToString(", ") { "'$it'" }
            jsonQueries += "S.json::jsonb->>'mostRecentState' IN ($states)"
        }
        // Filter on running state
        filter.running?.let { flag ->
            jsonQueries += "S.json::jsonb->>'running' = :running"
            params += "running" to flag.toString()
        }
        // Filter on source
        filter.source?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "S.json::jsonb->>'sourceProject' = :source"
            params += "source" to it
        }
        // Filter on version
        filter.version?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "S.json::jsonb->>'targetVersion' = :version"
            params += "version" to it
        }
        // Filter on routing
        filter.routing?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "S.json::jsonb->>'routing' = :routing"
            params += "routing" to it
        }
        // Filter on queue
        filter.queue?.takeIf { it.isNotBlank() }?.let {
            jsonQueries += "S.json::jsonb->>'queue' = :queue"
            params += "queue" to it
        }
        // JSON filter
        if (jsonQueries.isNotEmpty()) {
            query += " AND ${jsonQueries.joinToString(" AND ")}"
        }

        // Target project filter
        if (!filter.project.isNullOrBlank()) {
            joins += """ 
                INNER JOIN BRANCHES B ON B.ID = S.BRANCH 
                INNER JOIN PROJECTS P ON P.ID = B.PROJECTID
            """
            query += " AND P.NAME = :project"
            params += "project" to filter.project
            // Target branch filter
            if (!filter.branch.isNullOrBlank()) {
                query += " AND B.NAME = :branch"
                params += "branch" to filter.branch
            }
        }

        // Final query
        var sql = "$select FROM ENTITY_DATA_STORE S $joins $query"
        if (limits) {
            sql += " ORDER BY S.ID DESC LIMIT ${filter.count} OFFSET ${filter.offset}"
        }
        return sql
    }
}