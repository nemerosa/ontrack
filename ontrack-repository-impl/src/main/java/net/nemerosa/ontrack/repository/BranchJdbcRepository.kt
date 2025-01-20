package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchFilter
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class BranchJdbcRepository(
    dataSource: DataSource,
    private val projectJdbcRepositoryAccessor: ProjectJdbcRepositoryAccessor,
) : AbstractJdbcRepository(dataSource), BranchRepository, BranchJdbcRepositoryAccessor {

    override fun getBranch(id: ID, project: Project?): Branch =
        getFirstItem(
            """
               SELECT *
                FROM branches
                WHERE id = :id
            """,
            params("id", id.value)
        ) { rs, _ ->
            Branch(
                id = id(rs),
                name = rs.getString("name"),
                description = rs.getString("description"),
                isDisabled = rs.getBoolean("disabled"),
                project = project ?: projectJdbcRepositoryAccessor.getProject(id(rs, "projectid")),
                signature = readSignature(rs)
            )
        }

    override fun filterBranchesForProject(
        project: Project,
        user: ID?,
        filter: BranchFilter,
    ): List<Branch> {
        // Base SQL
        val root = """
            SELECT B.* 
            FROM BRANCHES B
        """
        // Base parameters
        val criterias = mutableListOf(
            "B.PROJECTID = :project"
        )
        val params: MutableMap<String, Any> = mutableMapOf(
            "project" to project.id()
        )
        // Extra tables
        val tables = mutableListOf<String>()
        // Default order clause
        var orderClause = "ORDER BY B.ID DESC"
        // Extra clauses at the end
        val finalClauses = mutableListOf<String>()

        // Filter: name
        val name = filter.name
        if (!name.isNullOrBlank()) {
            criterias += "B.NAME ~ :name"
            params["name"] = name
        }

        // Filter: excludes
        val excludes = filter.excludes
        if (!excludes.isNullOrBlank()) {
            criterias += "B.NAME !~ :excludes"
            params["excludes"] = excludes
        }

        // Filter: favorite
        val favorite = filter.favorite
        if (favorite != null && favorite && user != null) {
            tables += "INNER JOIN BRANCH_FAVOURITES BF ON BF.BRANCHID = B.ID"
            criterias += "BF.ACCOUNTID = :accountId"
            params["accountId"] = user.get()
        }

        // Filter: order
        if (filter.order) {
            tables += """
                LEFT JOIN (
                	SELECT DISTINCT ON (BRANCHID) BRANCHID, CREATION
                	FROM BUILDS
                	ORDER BY BRANCHID, CREATION DESC
                ) LAST_BUILD ON LAST_BUILD.BRANCHID = B.ID
            """.trimIndent()
            orderClause = "ORDER BY COALESCE(LAST_BUILD.CREATION, B.CREATION) DESC"
        }

        // Filter: enabled
        val enabled = filter.enabled
        if (enabled != null) {
            criterias += "B.DISABLED = :disabled"
            params["disabled"] = !enabled
        }

        // Filter: Count
        val count = filter.count
        if (count != null) {
            finalClauses += "LIMIT :count"
            params["count"] = count
        }

        // Final SQL
        val sqlFragments = listOf(root) +
                tables +
                listOf("WHERE") +
                listOf(criterias.joinToString("\nAND ") { "($it)" }) +
                listOf(orderClause) +
                finalClauses
        val sql = sqlFragments.joinToString("\n")

        // Running the query
        return namedParameterJdbcTemplate!!.query(
            sql,
            params
        ) { rs, _ ->
            Branch(
                id(rs),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("disabled"),
                project,
                readSignature(rs)
            )
        }
    }

}