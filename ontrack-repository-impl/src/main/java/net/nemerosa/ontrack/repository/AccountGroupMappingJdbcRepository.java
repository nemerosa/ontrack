package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupMapping;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
                        (rs, rowNum) -> {
                            return new AccountGroupMapping(
                                    id(rs),
                                    rs.getString("SOURCE"),
                                    accountGroupRepository.getById(id(rs, "GROUPID"))
                            );
                        }
                );
    }
}
