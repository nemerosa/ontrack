package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    public Collection<TBuildFilter> findForBranch(int branchId) {
        return order(getNamedParameterJdbcTemplate().query(
                "(SELECT * FROM BUILD_FILTERS WHERE BRANCHID = :branchId)" +
                        " UNION " +
                        "(SELECT NULL AS accountId, * FROM SHARED_BUILD_FILTERS WHERE BRANCHID = :branchId)",
                params("branchId", branchId),
                (rs, row) -> toBuildFilter(rs)
        ));
    }

    @Override
    public Collection<TBuildFilter> findForBranch(OptionalInt accountId, int branchId) {
        if (accountId.isPresent()) {
            return order(getNamedParameterJdbcTemplate().query(
                    "(SELECT * FROM BUILD_FILTERS WHERE ACCOUNTID = :accountId AND BRANCHID = :branchId)" +
                            " UNION " +
                            "(SELECT NULL AS accountId, * FROM SHARED_BUILD_FILTERS WHERE BRANCHID = :branchId)",
                    params("branchId", branchId).addValue("accountId", accountId.getAsInt()),
                    (rs, row) -> toBuildFilter(rs)
            ));
        } else {
            return getNamedParameterJdbcTemplate().query(
                    "SELECT NULL AS accountId, * FROM SHARED_BUILD_FILTERS WHERE BRANCHID = :branchId ORDER BY NAME",
                    params("branchId", branchId),
                    (rs, row) -> toBuildFilter(rs)
            );
        }
    }

    protected List<TBuildFilter> order(List<TBuildFilter> filters) {
        List<TBuildFilter> ordered = new ArrayList<>(filters);
        // Shared filters first
        // Order by name then
        Collections.sort(
                ordered,
                (o1, o2) -> {
                    OptionalInt a1 = o1.getAccountId();
                    OptionalInt a2 = o2.getAccountId();
                    if (a1.isPresent() == a2.isPresent()) {
                        return o1.getName().compareTo(o2.getName());
                    } else if (a1.isPresent()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
        );
        return ordered;
    }

    @Override
    public Optional<TBuildFilter> findByBranchAndName(int accountId, int branchId, String name) {
        MapSqlParameterSource params = params("branchId", branchId).addValue("accountId", accountId).addValue("name", name);
        // Looks first for shared filters
        Optional<TBuildFilter> shared = getOptional(
                "SELECT NULL AS accountId, * FROM SHARED_BUILD_FILTERS WHERE BRANCHID = :branchId AND NAME = :name",
                params,
                (rs, row) -> toBuildFilter(rs)
        );
        if (shared.isPresent()) {
            return shared;
        } else {
            return
                    getOptional(
                            "SELECT * FROM BUILD_FILTERS WHERE ACCOUNTID = :accountId AND BRANCHID = :branchId AND NAME = :name",
                            params,
                            (rs, row) -> toBuildFilter(rs)
                    );
        }
    }

    @Override
    public Ack save(OptionalInt accountId, int branchId, String name, String type, JsonNode data) {
        if (accountId.isPresent()) {
            MapSqlParameterSource params = params("branchId", branchId).addValue("accountId", accountId.getAsInt()).addValue("name", name);
            getNamedParameterJdbcTemplate().update(
                    "DELETE FROM BUILD_FILTERS WHERE ACCOUNTID = :accountId AND BRANCHID = :branchId AND NAME = :name",
                    params
            );
            return Ack.one(
                    getNamedParameterJdbcTemplate().update(
                            "INSERT INTO BUILD_FILTERS (ACCOUNTID, BRANCHID, NAME, TYPE, DATA) " +
                                    "VALUES (:accountId, :branchId, :name, :type, CAST(:data AS JSONB))",
                            params.addValue("type", type).addValue("data", writeJson(data))
                    )
            );
        } else {
            MapSqlParameterSource params = params("branchId", branchId).addValue("name", name);
            getNamedParameterJdbcTemplate().update(
                    "DELETE FROM SHARED_BUILD_FILTERS WHERE BRANCHID = :branchId AND NAME = :name",
                    params
            );
            return Ack.one(
                    getNamedParameterJdbcTemplate().update(
                            "INSERT INTO SHARED_BUILD_FILTERS (BRANCHID, NAME, TYPE, DATA) " +
                                    "VALUES (:branchId, :name, :type, CAST(:data AS JSONB))",
                            params.addValue("type", type).addValue("data", writeJson(data))
                    )
            );
        }
    }

    @Override
    public Ack delete(int accountId, int branchId, String name, boolean shared) {
        // Account filter
        Ack accountFilterDeleted = Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM BUILD_FILTERS WHERE ACCOUNTID = :accountId AND BRANCHID = :branchId AND NAME = :name",
                        params("branchId", branchId).addValue("accountId", accountId).addValue("name", name)
                )
        );
        // Shared filter
        if (shared) {
            return accountFilterDeleted.or(
                    Ack.one(
                            getNamedParameterJdbcTemplate().update(
                                    "DELETE FROM SHARED_BUILD_FILTERS WHERE BRANCHID = :branchId AND NAME = :name",
                                    params("branchId", branchId).addValue("name", name)
                            )
                    )
            );
        } else {
            return accountFilterDeleted;
        }
    }

    private TBuildFilter toBuildFilter(ResultSet rs) throws SQLException {
        return new TBuildFilter(
                optionalInt(rs, "accountId"),
                rs.getInt("branchId"),
                rs.getString("name"),
                rs.getString("type"),
                readJson(rs, "data")
        );
    }
}
