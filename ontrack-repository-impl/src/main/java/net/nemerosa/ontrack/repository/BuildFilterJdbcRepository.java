package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

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
                (rs, row) -> toBuildFilter(rs)
        );
    }

    @Override
    public Optional<TBuildFilter> findByBranchAndName(int accountId, int branchId, String name) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT * FROM BUILD_FILTERS WHERE ACCOUNTID = :accountId AND BRANCHID = :branchId AND NAME = :name",
                        params("branchId", branchId).addValue("accountId", accountId).addValue("name", name),
                        (rs, row) -> toBuildFilter(rs)
                )
        );
    }

    @Override
    public void save(int accountId, int branchId, String name, String type, JsonNode data) {
        MapSqlParameterSource params = params("branchId", branchId).addValue("accountId", accountId).addValue("name", name);
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM BUILD_FILTERS WHERE ACCOUNTID = :accountId AND BRANCHID = :branchId AND NAME = :name",
                params
        );
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO BUILD_FILTERS (ACCOUNTID, BRANCHID, NAME, TYPE, DATA) " +
                        "VALUES (:accountId, :branchId, :name, :type, :data)",
                params.addValue("type", type).addValue("data", writeJson(data))
        );
    }

    private TBuildFilter toBuildFilter(ResultSet rs) throws SQLException {
        return new TBuildFilter(
                rs.getInt("branchId"),
                rs.getString("name"),
                rs.getString("type"),
                readJson(rs, "data")
        );
    }
}
