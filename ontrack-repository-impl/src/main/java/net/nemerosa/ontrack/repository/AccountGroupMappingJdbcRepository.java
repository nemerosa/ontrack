package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AccountGroupMappingJdbcRepository extends AbstractJdbcRepository implements AccountGroupMappingRepository {

    @Autowired
    public AccountGroupMappingJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

}
