package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class StructureJdbcRepository extends AbstractJdbcRepository implements StructureRepository {

    private final ValidationRunStatusService validationRunStatusService;

    @Autowired
    public StructureJdbcRepository(DataSource dataSource, ValidationRunStatusService validationRunStatusService) {
        super(dataSource);
        this.validationRunStatusService = validationRunStatusService;
    }

    @Override
    public Project newProject(Project project) {
        // Creation
        try {
            int id = dbCreate(
                    "INSERT INTO PROJECTS(NAME, DESCRIPTION) VALUES (:name, :description)",
                    params("name", project.getName()).addValue("description", project.getDescription())
            );
            // Returns with ID
            return project.withId(id(id));
        } catch (DuplicateKeyException ex) {
            throw new ProjectNameAlreadyDefinedException(project.getName());
        }
    }

    @Override
    public List<Project> getProjectList() {
        return getJdbcTemplate().query(
                "SELECT * FROM PROJECTS ORDER BY NAME",
                (rs, rowNum) -> toProject(rs)
        );
    }

    @Override
    public Project getProject(ID projectId) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM PROJECTS WHERE ID = :id",
                    params("id", projectId.getValue()),
                    (rs, rowNum) -> toProject(rs)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new ProjectNotFoundException(projectId);
        }
    }

    @Override
    public Project getProjectByName(String project) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM PROJECTS WHERE NAME = :name",
                    params("name", project),
                    (rs, rowNum) -> toProject(rs)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new ProjectNotFoundException(project);
        }
    }

    @Override
    public void saveProject(Project project) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE PROJECTS SET NAME = :name, DESCRIPTION = :description WHERE ID = :id",
                params("name", project.getName())
                        .addValue("description", project.getDescription())
                        .addValue("id", project.getId().getValue())
        );
    }

    @Override
    public Ack deleteProject(ID projectId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM PROJECTS WHERE ID = :id",
                        params("id", projectId.getValue())
                )
        );
    }

    @Override
    public Branch getBranch(ID branchId) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM BRANCHES WHERE ID = :id",
                    params("id", branchId.getValue()),
                    (rs, rowNum) -> toBranch(rs, this::getProject)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new BranchNotFoundException(branchId);
        }
    }

    @Override
    public Branch getBranchByName(String project, String branch) {
        try {
            Project p = getProjectByName(project);
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM BRANCHES WHERE PROJECTID = :project AND NAME = :name",
                    params("name", branch).addValue("project", p.id()),
                    (rs, rowNum) -> toBranch(rs, id -> p)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new BranchNotFoundException(project, branch);
        }
    }

    @Override
    public List<Branch> getBranchesForProject(ID projectId) {
        Project project = getProject(projectId);
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM BRANCHES WHERE PROJECTID = :projectId ORDER BY NAME",
                params("projectId", projectId.getValue()),
                (rs, rowNum) -> toBranch(rs, id -> project)
        );
    }

    @Override
    public Branch newBranch(Branch branch) {
        // Creation
        try {
            int id = dbCreate(
                    "INSERT INTO BRANCHES(PROJECTID, NAME, DESCRIPTION) VALUES (:projectId, :name, :description)",
                    params("name", branch.getName())
                            .addValue("description", branch.getDescription())
                            .addValue("projectId", branch.getProject().id())
            );
            // Returns with ID
            return branch.withId(id(id));
        } catch (DuplicateKeyException ex) {
            throw new BranchNameAlreadyDefinedException(branch.getName());
        }
    }

    @Override
    public List<Build> builds(Branch branch, BuildFilter buildFilter) {
        // TODO The filter could contribute to the SQL to accelerate the search
        return getNamedParameterJdbcTemplate().execute(
                "SELECT * FROM BUILDS WHERE BRANCHID = :branchId ORDER BY ID DESC",
                params("branchId", branch.id()),
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    List<Build> builds = new ArrayList<>();
                    while (rs.next()) {
                        Build build = toBuild(
                                rs,
                                id -> branch
                        );
                        // TODO Filter on number of builds
                        // TODO Prefiltering without the promotions & validations
                        // TODO Promotion runs
                        // TODO Validation runs
                        // TODO Final filtering
                        // OK
                        builds.add(build);
                    }
                    // List
                    return builds;
                }
        );
    }

    @Override
    public Build getLastBuildForBranch(Branch branch) {
        return getFirstItem(
                "SELECT * FROM BUILDS WHERE BRANCHID = :branch ORDER BY ID DESC LIMIT 1",
                params("branch", branch.id()),
                (rs, num) -> toBuild(rs, (id) -> branch)
        );
    }

    protected Build toBuild(ResultSet rs, Function<ID, Branch> branchSupplier) throws SQLException {
        return Build.of(
                branchSupplier.apply(id(rs, "branchId")),
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                ),
                readSignature(rs)
        ).withId(id(rs));
    }

    @Override
    public Build newBuild(Build build) {
        // Creation
        try {
            int id = dbCreate(
                    "INSERT INTO BUILDS(BRANCHID, NAME, DESCRIPTION, CREATION, CREATOR) VALUES (:branchId, :name, :description, :creation, :creator)",
                    params("name", build.getName())
                            .addValue("description", build.getDescription())
                            .addValue("branchId", build.getBranch().id())
                            .addValue("creation", dateTimeForDB(build.getSignature().getTime()))
                            .addValue("creator", build.getSignature().getUser().getName())
            );
            return build.withId(id(id));
        } catch (DuplicateKeyException ex) {
            throw new BuildNameAlreadyDefinedException(build.getName());
        }
    }

    @Override
    public Build saveBuild(Build build) {
        // Update
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE BUILDS SET NAME = :name, DESCRIPTION = :description WHERE ID = :id",
                    params("name", build.getName())
                            .addValue("description", build.getDescription())
                            .addValue("id", build.id())
            );
            return build;
        } catch (DuplicateKeyException ex) {
            throw new BuildNameAlreadyDefinedException(build.getName());
        }
    }

    @Override
    public Build getBuild(ID buildId) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM BUILDS WHERE ID = :id",
                    params("id", buildId.getValue()),
                    (rs, rowNum) -> toBuild(rs, this::getBranch)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new BuildNotFoundException(buildId);
        }
    }


    @Override
    public Build getBuildByName(String project, String branch, String build) {
        try {
            Branch b = getBranchByName(project, branch);
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM BUILDS WHERE NAME = :name",
                    params("name", build),
                    (rs, rowNum) -> toBuild(rs, this::getBranch)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new BuildNotFoundException(project, branch, build);
        }
    }

    @Override
    public List<PromotionLevel> getPromotionLevelListForBranch(ID branchId) {
        Branch branch = getBranch(branchId);
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM PROMOTION_LEVELS WHERE BRANCHID = :branchId ORDER BY ORDERNB",
                params("branchId", branchId.getValue()),
                (rs, rowNum) -> toPromotionLevel(rs, id -> branch)
        );
    }

    @Override
    public PromotionLevel newPromotionLevel(PromotionLevel promotionLevel) {
        // Creation
        try {
            // Order nb = max + 1
            Integer orderNbValue = getFirstItem(
                    "SELECT MAX(ORDERNB) FROM promotion_levels WHERE BRANCHID = :branchId",
                    params("branchId", promotionLevel.getBranch().id()),
                    Integer.class
            );
            int orderNb = orderNbValue != null ? orderNbValue + 1 : 0;
            // Insertion
            int id = dbCreate(
                    "INSERT INTO PROMOTION_LEVELS(BRANCHID, NAME, DESCRIPTION, ORDERNB) VALUES (:branchId, :name, :description, :orderNb)",
                    params("name", promotionLevel.getName())
                            .addValue("description", promotionLevel.getDescription())
                            .addValue("branchId", promotionLevel.getBranch().id())
                            .addValue("orderNb", orderNb)
            );
            return promotionLevel.withId(id(id));
        } catch (DuplicateKeyException ex) {
            throw new PromotionLevelNameAlreadyDefinedException(promotionLevel.getName());
        }
    }

    @Override
    public PromotionLevel getPromotionLevel(ID promotionLevelId) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM PROMOTION_LEVELS WHERE ID = :id",
                    params("id", promotionLevelId.getValue()),
                    (rs, rowNum) -> toPromotionLevel(rs, this::getBranch)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new PromotionLevelNotFoundException(promotionLevelId);
        }
    }

    @Override
    public PromotionLevel getPromotionLevelByName(String project, String branch, String promotionLevel) {
        try {
            Branch b = getBranchByName(project, branch);
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM PROMOTION_LEVELS WHERE BRANCHID = :branch AND NAME = :name",
                    params("name", promotionLevel).addValue("branch", b.id()),
                    (rs, rowNum) -> toPromotionLevel(rs, this::getBranch)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new PromotionLevelNotFoundException(project, branch, promotionLevel);
        }
    }

    @Override
    public Document getPromotionLevelImage(ID promotionLevelId) {
        return getFirstItem(
                "SELECT IMAGETYPE, IMAGEBYTES FROM PROMOTION_LEVELS WHERE ID = :id",
                params("id", promotionLevelId.getValue()),
                (rs, rowNum) -> toDocument(rs)
        );
    }

    @Override
    public void setPromotionLevelImage(ID promotionLevelId, Document document) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE PROMOTION_LEVELS SET IMAGETYPE = :type, IMAGEBYTES = :content WHERE ID = :id",
                params("id", promotionLevelId.getValue())
                        .addValue("type", document != null ? document.getType() : null)
                        .addValue("content", document != null ? document.getContent() : null)
        );
    }

    @Override
    public PromotionRun newPromotionRun(PromotionRun promotionRun) {
        return promotionRun.withId(
                id(
                        dbCreate(
                                "INSERT INTO PROMOTION_RUNS(BUILDID, PROMOTIONLEVELID, CREATION, CREATOR, DESCRIPTION) VALUES (:buildId, :promotionLevelId, :creation, :creator, :description)",
                                params("buildId", promotionRun.getBuild().id())
                                        .addValue("promotionLevelId", promotionRun.getPromotionLevel().id())
                                        .addValue("description", promotionRun.getDescription())
                                        .addValue("creation", dateTimeForDB(promotionRun.getSignature().getTime()))
                                        .addValue("creator", promotionRun.getSignature().getUser().getName())
                        )
                )
        );
    }

    @Override
    public PromotionRun getPromotionRun(ID promotionRunId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM PROMOTION_RUNS WHERE ID = :id",
                params("id", promotionRunId.getValue()),
                (rs, rowNum) -> toPromotionRun(
                        rs,
                        this::getBuild,
                        this::getPromotionLevel
                )
        );
    }

    @Override
    public List<PromotionRun> getLastPromotionRunsForBuild(Build build) {
        // Branch
        Branch branch = build.getBranch();
        // Promotion levels for the branch
        List<PromotionLevel> promotionLevels = getPromotionLevelListForBranch(branch.getId());
        // Gets the last promotion run for each promotion level
        return promotionLevels.stream()
                .map(promotionLevel -> getLastPromotionRun(build, promotionLevel))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionRun getLastPromotionRunForPromotionLevel(PromotionLevel promotionLevel) {
        return getFirstItem(
                "SELECT * FROM PROMOTION_RUNS WHERE PROMOTIONLEVELID = :promotionLevelId ORDER BY CREATION DESC LIMIT 1",
                params("promotionLevelId", promotionLevel.id()),
                (rs, rowNum) -> toPromotionRun(rs,
                        this::getBuild,
                        (promotionLevelId) -> promotionLevel)
        );
    }

    protected Optional<PromotionRun> getLastPromotionRun(Build build, PromotionLevel promotionLevel) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT * FROM PROMOTION_RUNS WHERE BUILDID = :buildId AND PROMOTIONLEVELID = :promotionLevelId ORDER BY CREATION DESC LIMIT 1",
                        params("buildId", build.id()).addValue("promotionLevelId", promotionLevel.id()),
                        (rs, rowNum) -> toPromotionRun(rs,
                                (id) -> build,
                                (id) -> promotionLevel
                        )
                )
        );
    }

    protected PromotionRun toPromotionRun(ResultSet rs,
                                          Function<ID, Build> buildLoader,
                                          Function<ID, PromotionLevel> promotionLevelLoader) throws SQLException {
        return PromotionRun.of(
                buildLoader.apply(id(rs, "buildId")),
                promotionLevelLoader.apply(id(rs, "promotionLevelId")),
                readSignature(rs),
                rs.getString("description")
        );
    }

    @Override
    public List<ValidationStamp> getValidationStampListForBranch(ID branchId) {
        Branch branch = getBranch(branchId);
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_STAMPS WHERE BRANCHID = :branchId ORDER BY ORDERNB",
                params("branchId", branchId.getValue()),
                (rs, rowNum) -> toValidationStamp(rs, id -> branch)
        );
    }

    @Override
    public ValidationStamp newValidationStamp(ValidationStamp validationStamp) {
        // Creation
        try {
            // Order nb = max + 1
            Integer orderNbValue = getFirstItem(
                    "SELECT MAX(ORDERNB) FROM VALIDATION_STAMPS WHERE BRANCHID = :branchId",
                    params("branchId", validationStamp.getBranch().id()),
                    Integer.class
            );
            int orderNb = orderNbValue != null ? orderNbValue + 1 : 0;
            // Insertion
            int id = dbCreate(
                    "INSERT INTO VALIDATION_STAMPS(BRANCHID, NAME, DESCRIPTION, ORDERNB) VALUES (:branchId, :name, :description, :orderNb)",
                    params("name", validationStamp.getName())
                            .addValue("description", validationStamp.getDescription())
                            .addValue("branchId", validationStamp.getBranch().id())
                            .addValue("orderNb", orderNb)
            );
            return validationStamp.withId(id(id));
        } catch (DuplicateKeyException ex) {
            throw new ValidationStampNameAlreadyDefinedException(validationStamp.getName());
        }
    }

    @Override
    public ValidationStamp getValidationStamp(ID validationStampId) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM VALIDATION_STAMPS WHERE ID = :id",
                    params("id", validationStampId.getValue()),
                    (rs, rowNum) -> toValidationStamp(rs, this::getBranch)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationStampNotFoundException(validationStampId);
        }
    }

    @Override
    public ValidationStamp getValidationStampByName(String project, String branch, String validationStamp) {
        try {
            Branch b = getBranchByName(project, branch);
            return getNamedParameterJdbcTemplate().queryForObject(
                    "SELECT * FROM VALIDATION_STAMPS WHERE NAME = :name",
                    params("name", validationStamp).addValue("branch", b.id()),
                    (rs, rowNum) -> toValidationStamp(rs, id -> b)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidationStampNotFoundException(project, branch, validationStamp);
        }
    }

    @Override
    public Document getValidationStampImage(ID validationStampId) {
        return getFirstItem(
                "SELECT IMAGETYPE, IMAGEBYTES FROM VALIDATION_STAMPS WHERE ID = :id",
                params("id", validationStampId.getValue()),
                (rs, rowNum) -> toDocument(rs)
        );
    }

    @Override
    public void setValidationStampImage(ID validationStampId, Document document) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE VALIDATION_STAMPS SET IMAGETYPE = :type, IMAGEBYTES = :content WHERE ID = :id",
                params("id", validationStampId.getValue())
                        .addValue("type", document != null ? document.getType() : null)
                        .addValue("content", document != null ? document.getContent() : null)
        );
    }

    @Override
    public ValidationRun newValidationRun(ValidationRun validationRun) {

        // Validation run itself (parent)
        int id = dbCreate(
                "INSERT INTO VALIDATION_RUNS(BUILDID, VALIDATIONSTAMPID) VALUES (:buildId, :validationStampId)",
                params("buildId", validationRun.getBuild().id())
                        .addValue("validationStampId", validationRun.getValidationStamp().id())
        );

        // Statuses
        validationRun.getValidationRunStatuses().stream()
                .forEach(validationRunStatus -> newValidationRunStatus(id, validationRunStatus));

        // Reloads the run
        return getValidationRun(ID.of(id));
    }

    @Override
    public ValidationRun getValidationRun(ID validationRunId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM VALIDATION_RUNS WHERE ID = :id",
                params("id", validationRunId.getValue()),
                (rs, rowNum) -> toValidationRun(
                        rs,
                        this::getBuild,
                        this::getValidationStamp
                )
        );
    }

    @Override
    public List<ValidationRun> getValidationRunsForBuild(Build build) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_RUNS WHERE BUILDID = :buildId",
                params("buildId", build.id()),
                (rs, rowNum) -> toValidationRun(
                        rs,
                        id -> build,
                        this::getValidationStamp
                )
        );
    }

    @Override
    public List<ValidationRun> getValidationRunsForValidationStamp(ValidationStamp validationStamp, int offset, int count) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_RUNS WHERE VALIDATIONSTAMPID = :validationStampId ORDER BY ID DESC LIMIT :limit OFFSET :offset",
                params("validationStampId", validationStamp.id())
                        .addValue("limit", count)
                        .addValue("offset", offset),
                (rs, rowNum) -> toValidationRun(
                        rs,
                        this::getBuild,
                        id -> validationStamp
                )
        );
    }

    @Override
    public ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus) {
        // Saves the new status
        newValidationRunStatus(validationRun.id(), runStatus);
        // OK
        return validationRun.add(runStatus);
    }

    protected void newValidationRunStatus(int validationRunId, ValidationRunStatus validationRunStatus) {
        dbCreate(
                "INSERT INTO VALIDATION_RUN_STATUSES(VALIDATIONRUNID, VALIDATIONRUNSTATUSID, CREATION, CREATOR, DESCRIPTION) " +
                        "VALUES (:validationRunId, :validationRunStatusId, :creation, :creator, :description)",
                params("validationRunId", validationRunId)
                        .addValue("validationRunStatusId", validationRunStatus.getStatusID().getId())
                        .addValue("description", validationRunStatus.getDescription())
                        .addValue("creation", dateTimeForDB(validationRunStatus.getSignature().getTime()))
                        .addValue("creator", validationRunStatus.getSignature().getUser().getName())
        );
    }

    protected ValidationRun toValidationRun(ResultSet rs, Function<ID, Build> buildSupplier, Function<ID, ValidationStamp> validationStampSupplier) throws SQLException {
        int id = rs.getInt("id");
        // Statuses
        List<ValidationRunStatus> statuses = getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_RUN_STATUSES WHERE VALIDATIONRUNID = :validationRunId ORDER BY CREATION DESC",
                params("validationRunId", id),
                (rs1, rowNum) -> ValidationRunStatus.of(
                        readSignature(rs1),
                        validationRunStatusService.getValidationRunStatus(rs1.getString("validationRunStatusId")),
                        rs1.getString("description")
                )
        );
        // Build & validation stamp
        ID buildId = id(rs, "buildId");
        ID validationStampId = id(rs, "validationStampId");
        // Run order
        int runOrder = getNamedParameterJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM VALIDATION_RUNS WHERE BUILDID=:buildId AND VALIDATIONSTAMPID=:validationStampId AND ID <= :id",
                params("id", id).addValue("buildId", buildId.getValue()).addValue("validationStampId", validationStampId.getValue()),
                Integer.class
        );
        // Run itself
        return ValidationRun.of(
                buildSupplier.apply(buildId),
                validationStampSupplier.apply(validationStampId),
                runOrder,
                statuses
        ).withId(ID.of(id));
    }

    protected PromotionLevel toPromotionLevel(ResultSet rs, Function<ID, Branch> branchSupplier) throws SQLException {
        return PromotionLevel.of(
                branchSupplier.apply(id(rs, "branchId")),
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        ).withId(id(rs)).withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }

    protected ValidationStamp toValidationStamp(ResultSet rs, Function<ID, Branch> branchSupplier) throws SQLException {
        return ValidationStamp.of(
                branchSupplier.apply(id(rs, "branchId")),
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        ).withId(id(rs)).withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }

    protected Branch toBranch(ResultSet rs, Function<ID, Project> projectSupplier) throws SQLException {
        return Branch.of(
                projectSupplier.apply(id(rs, "projectId")),
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        ).withId(id(rs));
    }

    protected Project toProject(ResultSet rs) throws SQLException {
        return Project.of(new NameDescription(
                rs.getString("name"),
                rs.getString("description")
        )).withId(id(rs.getInt("id")));
    }

    protected Document toDocument(ResultSet rs) throws SQLException {
        String type = rs.getString("imagetype");
        byte[] bytes = rs.getBytes("imagebytes");
        if (StringUtils.isNotBlank(type) && bytes != null && bytes.length > 0) {
            return new Document(type, bytes);
        } else {
            return null;
        }
    }

}
