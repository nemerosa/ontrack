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

    @Override
    public int getBuildCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM BUILDS",
                Integer.class
        );
    }

    @Override
    public int getPromotionLevelCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM PROMOTION_LEVELS",
                Integer.class
        );
    }

    @Override
    public int getPromotionRunCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM PROMOTION_RUNS",
                Integer.class
        );
    }

    @Override
    public int getValidationStampCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM VALIDATION_STAMPS",
                Integer.class
        );
    }

    @Override
    public int getValidationRunCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM VALIDATION_RUNS",
                Integer.class
        );
    }

    @Override
    public int getValidationRunStatusCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM VALIDATION_RUN_STATUSES",
                Integer.class
        );
    }

    @Override
    public int getPropertyCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM PROPERTIES",
                Integer.class
        );
    }

    @Override
    public int getEventCount() {
        return getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM EVENTS",
                Integer.class
        );
    }
}
