package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class CoreBuildFilterJdbcRepository extends AbstractJdbcRepository implements CoreBuildFilterRepository {

    private final StructureRepository structureRepository;

    @Autowired
    public CoreBuildFilterJdbcRepository(DataSource dataSource, StructureRepository structureRepository) {
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
     *     BDFROM (builds linked from)
     *     BRFROM (branches linked from)
     *     PJFROM (projects linked from)
     *     PLFROM (promotions linked from)
     *     BDTO (builds linked to)
     *     BRTO (branches linked to)
     *     PJTO (projects linked to)
     *     PLTO (promotions linked to)
     * </pre>
     */
    @Override
    public List<Build> standardFilter(Branch branch, StandardBuildFilterData data) {
        // Query root
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
                // FROM BUILDS
                "                LEFT JOIN BUILD_LINKS BLFROM ON BLFROM.TARGETBUILDID = B.ID" +
                "                LEFT JOIN BUILDS BDFROM ON BDFROM.ID = BLFROM.BUILDID" +
                "                LEFT JOIN BRANCHES BRFROM ON BRFROM.ID = BDFROM.BRANCHID" +
                "                LEFT JOIN PROJECTS PJFROM ON PJFROM.ID = BRFROM.PROJECTID" +
                "                LEFT JOIN PROMOTION_RUNS PRFROM ON PRFROM.BUILDID = BDFROM.ID" +
                "                LEFT JOIN PROMOTION_LEVELS PLFROM ON PLFROM.ID = PRFROM.PROMOTIONLEVELID" +
                // TO BUILDS
                "                LEFT JOIN BUILD_LINKS BLTO ON BLTO.BUILDID = B.ID" +
                "                LEFT JOIN BUILDS BDTO ON BDTO.ID = BLTO.TARGETBUILDID" +
                "                LEFT JOIN BRANCHES BRTO ON BRTO.ID = BDTO.BRANCHID" +
                "                LEFT JOIN PROJECTS PJTO ON PJTO.ID = BRTO.PROJECTID" +
                "                LEFT JOIN PROMOTION_RUNS PRTO ON PRTO.BUILDID = BDTO.ID" +
                "                LEFT JOIN PROMOTION_LEVELS PLTO ON PLTO.ID = PRTO.PROMOTIONLEVELID" +
                // Branch criteria
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

        // afterDate
        LocalDate afterDate = data.getAfterDate();
        if (afterDate != null) {
            sql.append(" AND B.CREATION >= :afterDate");
            params.addValue("afterDate", dateTimeForDB(afterDate.atTime(0, 0)));
        }

        // beforeDate
        LocalDate beforeDate = data.getBeforeDate();
        if (beforeDate != null) {
            sql.append(" AND B.CREATION <= :beforeDate");
            params.addValue("beforeDate", dateTimeForDB(beforeDate.atTime(23, 59, 59)));
        }

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
                    sinceBuildId = Math.max(sinceBuildId, id);
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

        // withProperty
        String withProperty = data.getWithProperty();
        if (StringUtils.isNotBlank(withProperty)) {
            sql.append(" AND PP.TYPE = :withProperty");
            params.addValue("withProperty", withProperty);
            // withPropertyValue
            String withPropertyValue = data.getWithPropertyValue();
            if (StringUtils.isNotBlank(withPropertyValue)) {
                sql.append(" AND PP.SEARCHKEY REGEXP :withPropertyValue");
                params.addValue("withPropertyValue", withPropertyValue);
            }
        }

        // sinceProperty
        String sinceProperty = data.getSinceProperty();
        if (StringUtils.isNotBlank(sinceProperty)) {
            String sincePropertyValue = data.getSincePropertyValue();
            Integer id = findLastBuildWithPropertyValue(branch, sinceProperty, sincePropertyValue);
            if (id != null) {
                if (sinceBuildId == null) {
                    sinceBuildId = id;
                } else {
                    sinceBuildId = Math.max(sinceBuildId, id);
                }
            }
        }

        // linkedFrom
        String linkedFrom = data.getLinkedFrom();
        if (isNotBlank(linkedFrom)) {
            String project = StringUtils.substringBefore(linkedFrom, ":");
            sql.append(" AND PJFROM.NAME = :fromProject");
            params.addValue("fromProject", project);
            String buildPattern = StringUtils.substringAfter(linkedFrom, ":");
            if (StringUtils.isNotBlank(buildPattern)) {
                if (StringUtils.contains(buildPattern, "*")) {
                    sql.append(" AND BDFROM.NAME LIKE :buildFrom");
                    params.addValue("buildFrom", StringUtils.replace(buildPattern, "*", "%"));
                } else {
                    sql.append(" AND BDFROM.NAME = :buildFrom");
                    params.addValue("buildFrom", buildPattern);
                }
            }
            // linkedFromPromotion
            String linkedFromPromotion = data.getLinkedFromPromotion();
            if (StringUtils.isNotBlank(linkedFromPromotion)) {
                sql.append(" AND PLFROM.NAME = :linkedFromPromotion");
                params.addValue("linkedFromPromotion", linkedFromPromotion);
            }
        }

        // linkedTo
        String linkedTo = data.getLinkedTo();
        if (isNotBlank(linkedTo)) {
            String project = StringUtils.substringBefore(linkedTo, ":");
            sql.append(" AND PJTO.NAME = :toProject");
            params.addValue("toProject", project);
            String buildPattern = StringUtils.substringAfter(linkedTo, ":");
            if (StringUtils.isNotBlank(buildPattern)) {
                if (StringUtils.contains(buildPattern, "*")) {
                    sql.append(" AND BDTO.NAME LIKE :buildTo");
                    params.addValue("buildTo", StringUtils.replace(buildPattern, "*", "%"));
                } else {
                    sql.append(" AND BDTO.NAME = :buildTo");
                    params.addValue("buildTo", buildPattern);
                }
            }
            // linkedToPromotion
            String linkedToPromotion = data.getLinkedToPromotion();
            if (StringUtils.isNotBlank(linkedToPromotion)) {
                sql.append(" AND PLTO.NAME = :linkedToPromotion");
                params.addValue("linkedToPromotion", linkedToPromotion);
            }
        }

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
        return loadBuilds(sql.toString(), params);
    }

    private List<Build> loadBuilds(String sql, MapSqlParameterSource params) {
        return getNamedParameterJdbcTemplate()
                .queryForList(
                        sql,
                        params,
                        Integer.class
                )
                .stream()
                .map(id -> structureRepository.getBuild(ID.of(id)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Build> nameFilter(Branch branch, String fromBuild, String toBuild, String withPromotionLevel, int count) {
        // Query root
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(B.ID) FROM BUILDS B" +
                "                LEFT JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID" +
                "                LEFT JOIN PROMOTION_LEVELS PL ON PL.ID = PR.PROMOTIONLEVELID" +
                // Branch criteria
                "                WHERE B.BRANCHID = :branch");

        // Parameters
        MapSqlParameterSource params = new MapSqlParameterSource("branch", branch.id());

        // From build
        Optional<Integer> fromBuildId = lastBuild(branch, fromBuild, null)
                .map(Entity::id);
        if (!fromBuildId.isPresent()) {
            return Collections.emptyList();
        }
        sql.append(" AND B.ID >= :fromBuildId");
        params.addValue("fromBuildId", fromBuildId.get());

        // To build
        if (StringUtils.isNotBlank(toBuild)) {
            Optional<Integer> toBuildId = lastBuild(branch, toBuild, null).map(Entity::id);
            if (toBuildId.isPresent()) {
                sql.append(" AND B.ID <= :toBuildId");
                params.addValue("toBuildId", toBuildId.get());
            }
        }

        // With promotion
        if (StringUtils.isNotBlank(withPromotionLevel)) {
            sql.append(" AND PL.NAME = :withPromotionLevel");
            params.addValue("withPromotionLevel", withPromotionLevel);
        }

        // Ordering
        sql.append(" ORDER BY B.ID DESC");
        // Limit
        sql.append(" LIMIT :count");
        params.addValue("count", count);

        // Running the query
        return loadBuilds(sql.toString(), params);
    }

    @Override
    public Optional<Build> lastBuild(Branch branch, String sinceBuild, String withPromotionLevel) {
        // Query root
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(B.ID) FROM BUILDS B" +
                "                LEFT JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID" +
                "                LEFT JOIN PROMOTION_LEVELS PL ON PL.ID = PR.PROMOTIONLEVELID" +
                // Branch criteria
                "                WHERE B.BRANCHID = :branch");

        // Parameters
        MapSqlParameterSource params = new MapSqlParameterSource("branch", branch.id());

        // Since build
        if (StringUtils.contains(sinceBuild, "*")) {
            sql.append(" AND B.NAME LIKE :buildName");
            params.addValue("buildName", StringUtils.replace(sinceBuild, "*", "%"));
        } else {
            sql.append(" AND B.NAME = :buildName");
            params.addValue("buildName", sinceBuild);
        }

        // With promotion
        if (StringUtils.isNotBlank(withPromotionLevel)) {
            sql.append(" AND PL.NAME = :withPromotionLevel");
            params.addValue("withPromotionLevel", withPromotionLevel);
        }

        // Ordering
        sql.append(" ORDER BY B.ID DESC");
        // Limit
        sql.append(" LIMIT 1");

        // Running the query
        return loadBuilds(sql.toString(), params)
                .stream()
                .findFirst();
    }

    @Override
    public List<Build> between(Branch branch, String from, String to) {
        StringBuilder sql = new StringBuilder(
                "SELECT ID FROM BUILDS WHERE " +
                        "BRANCHID = :branchId "
        );
        MapSqlParameterSource params = params("branchId", branch.id());
        Integer fromId;
        Integer toId = null;

        // From build
        fromId = structureRepository.getBuildByName(branch.getProject().getName(), branch.getName(), from)
                .orElseThrow(() -> new BuildNotFoundException(branch.getProject().getName(), branch.getName(), from))
                .id();

        // To build
        if (StringUtils.isNotBlank(to)) {
            toId = structureRepository.getBuildByName(branch.getProject().getName(), branch.getName(), to)
                    .orElseThrow(() -> new BuildNotFoundException(branch.getProject().getName(), branch.getName(), to))
                    .id();
        }

        if (toId != null) {
            if (toId < fromId) {
                int i = toId;
                toId = fromId;
                fromId = i;
            }
        }

        sql.append(" AND ID >= :fromId");
        params.addValue("fromId", fromId);
        if (toId != null) {
            sql.append(" AND ID <= :toId");
            params.addValue("toId", toId);
        }

        // Ordering
        sql.append(" ORDER BY ID DESC");

        // Query
        return loadBuilds(sql.toString(), params);
    }

    private Integer findLastBuildWithPropertyValue(Branch branch, String propertyType, String propertyValue) {
        // SQL
        StringBuilder sql = new StringBuilder("SELECT B.ID " +
                "FROM BUILDS B " +
                "LEFT JOIN PROPERTIES PP ON PP.BUILD = B.ID " +
                "WHERE B.BRANCHID = :branchId " +
                "AND PP.TYPE = :propertyType ");
        MapSqlParameterSource params = params("branchId", branch.id())
                .addValue("propertyType", propertyType);
        // Property value
        if (StringUtils.isNotBlank(propertyValue)) {
            sql.append(" AND PP.SEARCHKEY REGEXP :propertyValue");
            params.addValue("propertyValue", propertyValue);
        }
        // Ordering
        sql.append(" ORDER BY B.ID DESC LIMIT 1");
        // Build ID
        return getFirstItem(
                sql.toString(),
                params,
                Integer.class
        );
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
            sql.append("AND VRS.VALIDATIONRUNSTATUSID = :status\n");
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
