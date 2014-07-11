package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * <pre>
 * BUILD_FILTERS --
 * ACCOUNTID INTEGER      NOT NULL
 * BRANCHID  INTEGER      NOT NULL
 * NAME      VARCHAR(120) NOT NULL
 * TYPE      VARCHAR(150) NOT NULL
 * DATA      TEXT         NOT NULL
 * </pre>
 */
@Repository
public class BuildFilterJdbcRepository extends AbstractJdbcRepository implements BuildFilterRepository {

    @Autowired
    public BuildFilterJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Collection<TBuildFilter> findForBranch(int accountId, int branchId) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM BUILD_FILTERS WHERE ACCOUNTID = :accountId AND BRANCHID = :branchId",
                params("branchId", branchId).addValue("accountId", accountId),
                (rs, row) -> new TBuildFilter(
                        rs.getInt("branchId"),
                        rs.getString("name"),
                        rs.getString("type"),
                        readJson(rs, "data")
                )
        );
    }
}
