package net.nemerosa.ontrack.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PropertyJdbcRepository extends AbstractJdbcRepository implements PropertyRepository {

    @Autowired
    public PropertyJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }
}
