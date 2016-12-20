package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StandardBuildFilterJdbcRepository extends AbstractJdbcRepository implements StandardBuildFilterRepository {

    private final StructureRepository structureRepository;

    @Autowired
    public StandardBuildFilterJdbcRepository(DataSource dataSource, StructureRepository structureRepository) {
        super(dataSource);
        this.structureRepository = structureRepository;
    }

    @Override
    public List<Build> getBuilds(ID branchId, StandardBuildFilterData data) {
        // Query root
        // TODO Builds linked from
        // TODO Builds linked to
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(B.ID) FROM BUILDS B" +
                "                LEFT JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID" +
                "                LEFT JOIN PROMOTION_LEVELS PL ON PL.ID = PR.PROMOTIONLEVELID" +
                "                LEFT JOIN (" +
                "                    SELECT R.BUILDID,  R.VALIDATIONSTAMPID, VRS.VALIDATIONRUNSTATUSID " +
                "                    FROM VALIDATION_RUNS R" +
                "                    INNER JOIN VALIDATION_RUN_STATUSES VRS ON VRS.ID = (SELECT ID FROM VALIDATION_RUN_STATUSES WHERE VALIDATIONRUNID = R.ID ORDER BY ID DESC LIMIT 1)" +
                "                    AND R.ID = (SELECT MAX(ID) FROM VALIDATION_RUNS WHERE BUILDID = R.BUILDID AND VALIDATIONSTAMPID = R.VALIDATION_STAMPID)" +
                "                    ) S ON S.BUILDID = B.ID" +
                "                LEFT JOIN PROPERTIES PP ON PP.BUILD = B.ID" +
                "                WHERE B.BRANCHID = :branch");
        // Parameters
        MapSqlParameterSource params = new MapSqlParameterSource("branch", branchId);

        // FIXME sincePromotionLevel
        // FIXME withPromotionLevel
        // FIXME afterDate
        // FIXME beforeDate
        // FIXME sinceValidationStamp
        // FIXME sinceValidationStampStatus
        // FIXME withValidationStamp
        // FIXME withValidationStampStatus
        // FIXME withProperty
        // FIXME withPropertyValue
        // FIXME sinceProperty
        // FIXME sincePropertyValue
        // FIXME linkedFrom
        // FIXME linkedTo

        // Ordering
        sql.append(" ORDER BY B.ID DESC");
        // Limit
        sql.append(" LIMIT :count");
        params.addValue("count", data.getCount());

        // Running the query
        return getNamedParameterJdbcTemplate()
                .queryForList(
                        sql.toString(),
                        params,
                        Integer.class
                )
                .stream()
                .map(id -> structureRepository.getBuild(ID.of(id)))
                .collect(Collectors.toList());
    }
}
