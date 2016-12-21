package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * The fat join query (dreaming of Neo4J) defines the following columns:
     * <p>
     * <pre>
     *     B (BUILDS)
     *     PR (PROMOTION_RUNS)
     *     PL (PROMOTION_LEVELS)
     *     S (last validation run status)
     *          VALIDATIONSTAMPID
     *          VALIDATIONRUNSTATUSID
     *     PP (PROPERTIES)
     * </pre>
     */
    @Override
    public List<Build> getBuilds(Branch branch, StandardBuildFilterData data) {
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
                "                    AND R.ID = (SELECT MAX(ID) FROM VALIDATION_RUNS WHERE BUILDID = R.BUILDID AND VALIDATIONSTAMPID = R.VALIDATIONSTAMPID)" +
                "                    ) S ON S.BUILDID = B.ID" +
                "                LEFT JOIN PROPERTIES PP ON PP.BUILD = B.ID" +
                "                WHERE B.BRANCHID = :branch");

        // Parameters
        MapSqlParameterSource params = new MapSqlParameterSource("branch", branch.id());
        Integer sinceBuildId = null;

        // sincePromotionLevel
        String sincePromotionLevel = data.getSincePromotionLevel();
        if (StringUtils.isNotBlank(sincePromotionLevel)) {
            // Gets the promotion level ID
            int promotionLevelId = structureRepository
                    .getPromotionLevelByName(branch, sincePromotionLevel)
                    .map(Entity::id)
                    .orElseThrow(() -> new PromotionLevelNotFoundException(
                            branch.getProject().getName(),
                            branch.getName(),
                            sincePromotionLevel
                    ));
            // Gets the last build having this promotion level
            Integer id = findLastBuildWithPromotionLevel(promotionLevelId);
            if (id != null) {
                sinceBuildId = id;
            }
        }

        // withPromotionLevel
        String withPromotionLevel = data.getWithPromotionLevel();
        if (StringUtils.isNotBlank(withPromotionLevel)) {
            sql.append(" AND PL.NAME = :withPromotionLevel");
            params.addValue("withPromotionLevel", withPromotionLevel);
        }

        // FIXME afterDate
        // FIXME beforeDate

        // sinceValidationStamp
        String sinceValidationStamp = data.getSinceValidationStamp();
        if (StringUtils.isNotBlank(sinceValidationStamp)) {
            // Gets the validation stamp ID
            int validationStampId = getValidationStampId(branch, sinceValidationStamp);
            // Gets the last build having this validation stamp and the validation status
            Integer id = findLastBuildWithValidationStamp(validationStampId, data.getSinceValidationStampStatus());
            if (id != null) {
                if (sinceBuildId == null) {
                    sinceBuildId = id;
                } else {
                    sinceBuildId = Math.min(sinceBuildId, id);
                }
            }
        }

        // withValidationStamp
        String withValidationStamp = data.getWithValidationStamp();
        if (StringUtils.isNotBlank(withValidationStamp)) {
            // Gets the validation stamp ID
            int validationStampId = getValidationStampId(branch, withValidationStamp);
            sql.append(" AND (S.VALIDATIONSTAMPID = :validationStampId");
            params.addValue("validationStampId", validationStampId);
            // withValidationStampStatus
            String withValidationStampStatus = data.getWithValidationStampStatus();
            if (StringUtils.isNotBlank(withValidationStampStatus)) {
                sql.append(" AND S.VALIDATIONRUNSTATUSID = :withValidationStampStatus");
                params.addValue("withValidationStampStatus", withValidationStampStatus);
            }
            sql.append(")");
        }

        // FIXME withProperty
        // FIXME withPropertyValue
        // FIXME sinceProperty
        // FIXME sincePropertyValue
        // FIXME linkedFrom
        // FIXME linkedTo

        // Since build?
        if (sinceBuildId != null) {
            sql.append(" AND B.ID >= :sinceBuildId");
            params.addValue("sinceBuildId", sinceBuildId);
        }

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

    private Integer getValidationStampId(Branch branch, String validationStampName) {
        return structureRepository
                .getValidationStampByName(branch, validationStampName)
                .map(Entity::id)
                .orElseThrow(() -> new ValidationStampNotFoundException(
                        branch.getProject().getName(),
                        branch.getName(),
                        validationStampName
                ));
    }

    private Integer findLastBuildWithValidationStamp(int validationStampId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT VR.BUILDID FROM VALIDATION_RUN_STATUSES VRS\n" +
                        "INNER JOIN VALIDATION_RUNS VR ON VR.ID = VRS.VALIDATIONRUNID\n" +
                        "WHERE VR.VALIDATIONSTAMPID = :validationStampId\n"
        );
        // Parameters
        MapSqlParameterSource params = params("validationStampId", validationStampId);
        // Status criteria
        if (StringUtils.isNotBlank(status)) {
            sql.append("AND VRS.STATUS = :status\n");
            params.addValue("status", status);
        }
        // Order & limit
        sql.append("ORDER BY VR.BUILDID DESC LIMIT 1\n");
        // Build ID
        return getFirstItem(
                sql.toString(),
                params,
                Integer.class
        );
    }

    private Integer findLastBuildWithPromotionLevel(int promotionLevelId) {
        return getFirstItem(
                "SELECT BUILDID FROM PROMOTION_RUNS WHERE PROMOTIONLEVELID = :promotionLevelId ORDER BY BUILDID DESC LIMIT 1",
                params("promotionLevelId", promotionLevelId),
                Integer.class
        );
    }
}
