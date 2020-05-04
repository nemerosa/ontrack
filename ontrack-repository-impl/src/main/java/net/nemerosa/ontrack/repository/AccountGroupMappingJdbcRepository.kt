package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingNameAlreadyDefinedException
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingNotFoundException
import net.nemerosa.ontrack.model.security.*
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
        private val accountGroupRepository: AccountGroupRepository,
        private val authenticationSourceRepository: AuthenticationSourceRepository
) : AbstractJdbcRepository(dataSource), AccountGroupMappingRepository {

    override fun getGroups(authenticationSource: AuthenticationSource, origin: String): Collection<AccountGroup> {
        return namedParameterJdbcTemplate!!
                .queryForList(
                        "SELECT GROUPID FROM ACCOUNT_GROUP_MAPPING WHERE PROVIDER = :provider AND SOURCE = :source AND ORIGIN = :origin",
                        authenticationSource.asParams()
                                .addValue("origin", origin),
                        Int::class.java
                )
                .map { groupId: Int -> accountGroupRepository.getById(of(groupId)) }
    }

    override fun getMappings(authenticationSource: AuthenticationSource): List<AccountGroupMapping> {
        return namedParameterJdbcTemplate!!
                .query(
                        "SELECT * FROM ACCOUNT_GROUP_MAPPING WHERE PROVIDER = :provider AND SOURCE = :source  ORDER BY ORIGIN, PROVIDER, SOURCE",
                        authenticationSource.asParams()
                ) { rs: ResultSet, rowNum: Int ->
                    toAccountGroupMapping(rs, rowNum)
                }
    }

    override fun findAll(): List<AccountGroupMapping> {
        return jdbcTemplate!!.query(
                "SELECT * FROM ACCOUNT_GROUP_MAPPING ORDER BY ORIGIN, PROVIDER, SOURCE"
        ) { rs: ResultSet, rowNum: Int ->
            toAccountGroupMapping(rs, rowNum)
        }
    }

    override fun newMapping(authenticationSource: AuthenticationSource, input: AccountGroupMappingInput): AccountGroupMapping {
        return try {
            getMapping(
                    of(
                            dbCreate(
                                    "INSERT INTO ACCOUNT_GROUP_MAPPING(PROVIDER, SOURCE, ORIGIN, GROUPID) " +
                                            "VALUES(:provider, :source, :origin, :groupId)",
                                    authenticationSource.asParams()
                                            .addValue("origin", input.name)
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
        val provider = rs.getString("PROVIDER")
        val source = rs.getString("SOURCE")
        val authenticationSource = authenticationSourceRepository.getRequiredAuthenticationSource(provider, source)
        return AccountGroupMapping(
                id(rs),
                authenticationSource,
                rs.getString("ORIGIN"),
                accountGroupRepository.getById(id(rs, "GROUPID"))
        )
    }

}