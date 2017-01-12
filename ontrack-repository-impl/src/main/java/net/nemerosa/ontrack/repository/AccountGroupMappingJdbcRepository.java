package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingNotFoundException;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupMapping;
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccountGroupMappingJdbcRepository extends AbstractJdbcRepository implements AccountGroupMappingRepository {

    private final AccountGroupRepository accountGroupRepository;

    @Autowired
    public AccountGroupMappingJdbcRepository(DataSource dataSource, AccountGroupRepository accountGroupRepository) {
        super(dataSource);
        this.accountGroupRepository = accountGroupRepository;
    }

    @Override
    public Collection<AccountGroup> getGroups(String mapping, String mappedName) {
        return getNamedParameterJdbcTemplate()
                .queryForList(
                        "SELECT GROUPID FROM ACCOUNT_GROUP_MAPPING WHERE MAPPING = :mapping AND SOURCE = :mappedName",
                        params("mapping", mapping).addValue("mappedName", mappedName),
                        Integer.class
                )
                .stream()
                .map(groupId -> accountGroupRepository.getById(ID.of(groupId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountGroupMapping> getMappings(String mapping) {
        return getNamedParameterJdbcTemplate()
                .query(
                        "SELECT * FROM ACCOUNT_GROUP_MAPPING WHERE MAPPING = :mapping ORDER BY SOURCE",
                        params("mapping", mapping),
                        this::toAccountGroupMapping
                );
    }

    @Override
    public AccountGroupMapping newMapping(String mapping, AccountGroupMappingInput input) {
        try {
            return getMapping(
                    ID.of(dbCreate(
                            "INSERT INTO ACCOUNT_GROUP_MAPPING(MAPPING, SOURCE, GROUPID) " +
                                    "VALUES(:mapping, :source, :groupId)",
                            params("mapping", mapping)
                                    .addValue("source", input.getName())
                                    .addValue("groupId", input.getGroup().get())
                            )
                    )
            );
        } catch (DuplicateKeyException ex) {
            throw new AccountGroupMappingNameAlreadyDefinedException(input.getName());
        }
    }

    @Override
    public AccountGroupMapping getMapping(ID id) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM ACCOUNT_GROUP_MAPPING WHERE ID = :id",
                    params("id", id.get()),
                    this::toAccountGroupMapping
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new AccountGroupMappingNotFoundException(id);
        }
    }

    @Override
    public AccountGroupMapping updateMapping(ID id, AccountGroupMappingInput input) {
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE ACCOUNT_GROUP_MAPPING SET SOURCE = :source, GROUPID = :groupId WHERE ID = :id",
                    params("id", id.get())
                            .addValue("source", input.getName())
                            .addValue("groupId", input.getGroup().get())
            );
            return getMapping(id);
        } catch (DuplicateKeyException ex) {
            throw new AccountGroupMappingNameAlreadyDefinedException(input.getName());
        }
    }

    @Override
    public Ack deleteMapping(ID id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM ACCOUNT_GROUP_MAPPING WHERE ID = :id",
                        params("id", id.get())
                )
        );
    }

    @Override
    public List<AccountGroupMapping> getMappingsForGroup(AccountGroup group) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM ACCOUNT_GROUP_MAPPING WHERE GROUPID = :groupId",
                params("groupId", group.id()),
                this::toAccountGroupMapping
        );
    }

    protected AccountGroupMapping toAccountGroupMapping(ResultSet rs, int rowNum) throws SQLException {
        return new AccountGroupMapping(
                id(rs),
                rs.getString("MAPPING"),
                rs.getString("SOURCE"),
                accountGroupRepository.getById(id(rs, "GROUPID"))
        );
    }
}
