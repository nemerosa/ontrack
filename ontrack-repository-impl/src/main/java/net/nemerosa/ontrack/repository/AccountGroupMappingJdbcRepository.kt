package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingNameAlreadyDefinedException
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingNotFoundException
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountGroupMapping
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

@Repository
class AccountGroupMappingJdbcRepository(
        dataSource: DataSource,
        private val accountGroupRepository: AccountGroupRepository
) : AbstractJdbcRepository(dataSource), AccountGroupMappingRepository {

    override fun getGroups(mapping: String, mappedName: String): Collection<AccountGroup> {
        return namedParameterJdbcTemplate!!
                .queryForList(
                        "SELECT GROUPID FROM ACCOUNT_GROUP_MAPPING WHERE MAPPING = :mapping AND SOURCE = :mappedName",
                        params("mapping", mapping).addValue("mappedName", mappedName),
                        Int::class.java
                )
                .map { groupId: Int -> accountGroupRepository.getById(of(groupId)) }
    }

    override fun getMappings(mapping: String): List<AccountGroupMapping> {
        return namedParameterJdbcTemplate!!
                .query(
                        "SELECT * FROM ACCOUNT_GROUP_MAPPING WHERE MAPPING = :mapping ORDER BY SOURCE",
                        params("mapping", mapping)) { rs: ResultSet, rowNum: Int ->
                    toAccountGroupMapping(rs, rowNum)
                }
    }

    override fun newMapping(mapping: String, input: AccountGroupMappingInput): AccountGroupMapping {
        return try {
            getMapping(
                    of(
                            dbCreate(
                                    "INSERT INTO ACCOUNT_GROUP_MAPPING(MAPPING, SOURCE, GROUPID) " +
                                            "VALUES(:mapping, :source, :groupId)",
                                    params("mapping", mapping)
                                            .addValue("source", input.name)
                                            .addValue("groupId", input.group.get())
                            )
                    )
            )
        } catch (ex: DuplicateKeyException) {
            throw AccountGroupMappingNameAlreadyDefinedException(input.name)
        }
    }

    override fun getMapping(id: ID): AccountGroupMapping {
        return try {
            namedParameterJdbcTemplate!!.queryForObject(
                    "SELECT * FROM ACCOUNT_GROUP_MAPPING WHERE ID = :id",
                    params("id", id.get())) { rs: ResultSet, rowNum: Int -> toAccountGroupMapping(rs, rowNum) }!!
        } catch (ex: EmptyResultDataAccessException) {
            throw AccountGroupMappingNotFoundException(id)
        }
    }

    override fun updateMapping(id: ID, input: AccountGroupMappingInput): AccountGroupMapping {
        return try {
            namedParameterJdbcTemplate!!.update(
                    "UPDATE ACCOUNT_GROUP_MAPPING SET SOURCE = :source, GROUPID = :groupId WHERE ID = :id",
                    params("id", id.get())
                            .addValue("source", input.name)
                            .addValue("groupId", input.group.get())
            )
            getMapping(id)
        } catch (ex: DuplicateKeyException) {
            throw AccountGroupMappingNameAlreadyDefinedException(input.name)
        }
    }

    override fun deleteMapping(id: ID): Ack {
        return Ack.one(
                namedParameterJdbcTemplate!!.update(
                        "DELETE FROM ACCOUNT_GROUP_MAPPING WHERE ID = :id",
                        params("id", id.get())
                )
        )
    }

    override fun getMappingsForGroup(group: AccountGroup): List<AccountGroupMapping> {
        return namedParameterJdbcTemplate!!.query(
                "SELECT * FROM ACCOUNT_GROUP_MAPPING WHERE GROUPID = :groupId",
                params("groupId", group.id())) { rs: ResultSet, rowNum: Int -> toAccountGroupMapping(rs, rowNum) }
    }

    @Throws(SQLException::class)
    protected fun toAccountGroupMapping(rs: ResultSet, rowNum: Int): AccountGroupMapping {
        return AccountGroupMapping(
                id(rs),
                rs.getString("MAPPING"),
                rs.getString("SOURCE"),
                accountGroupRepository.getById(id(rs, "GROUPID"))
        )
    }

}