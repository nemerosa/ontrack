package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.GroupMapping
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class GroupMappingJdbcRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource), GroupMappingRepository {

    override fun mapGroup(idpGroup: String, groupID: Int?) {
        if (groupID == null) {
            namedParameterJdbcTemplate!!.update(
                """
                        DELETE FROM GROUP_MAPPINGS
                        WHERE IDP_GROUP = :idpGroup
                    """,
                mapOf(
                    "idpGroup" to idpGroup,
                )
            )
        } else {
            val existingGroupId = getMappedGroupId(idpGroup)
            if (existingGroupId != null) {
                if (existingGroupId != groupID) {
                    namedParameterJdbcTemplate!!.update(
                        """
                        UPDATE GROUP_MAPPINGS
                        SET GROUP_ID = :accountGroupId
                        WHERE IDP_GROUP = :idpGroup
                    """,
                        mapOf(
                            "accountGroupId" to groupID,
                            "idpGroup" to idpGroup,
                        )
                    )
                }
            } else {
                namedParameterJdbcTemplate!!.update(
                    """
                    INSERT INTO GROUP_MAPPINGS (IDP_GROUP, GROUP_ID) 
                    VALUES (:idpGroup, :accountGroupId)
                """,
                    mapOf(
                        "accountGroupId" to groupID,
                        "idpGroup" to idpGroup,
                    )
                )
            }
        }
    }

    override fun getMappedGroupId(idpGroup: String): Int? {
        return getFirstItem(
            """
               SELECT GROUP_ID
               FROM GROUP_MAPPINGS
               WHERE IDP_GROUP = :idpGroup
            """,
            mapOf(
                "idpGroup" to idpGroup,
            ),
            Int::class.java
        )
    }

    override fun getMappings(groupFn: (Int) -> AccountGroup): List<GroupMapping> = jdbcTemplate!!.query(
        """
                    SELECT *
                    FROM GROUP_MAPPINGS
                    ORDER BY IDP_GROUP
            """,
    ) { rs, _ ->
        GroupMapping(
            idpGroup = rs.getString("IDP_GROUP"),
            group = groupFn(rs.getInt("GROUP_ID"))
        )
    }
}