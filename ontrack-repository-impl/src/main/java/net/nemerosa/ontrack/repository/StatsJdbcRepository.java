package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class StatsJdbcRepository extends AbstractJdbcRepository implements StatsRepository {

    @Autowired
    public StatsJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int getProjectCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM PROJECTS",
                Integer.class
        );
    }

    @Override
    public int getBranchCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM BRANCHES",
                Integer.class
        );
    }
}
