package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.common.Document;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class StructureJdbcRepository extends AbstractJdbcRepository implements StructureRepository {

    private final BranchTemplateRepository branchTemplateRepository;

    @Autowired
    public StructureJdbcRepository(DataSource dataSource, BranchTemplateRepository branchTemplateRepository) {
        super(dataSource);
        this.branchTemplateRepository = branchTemplateRepository;
    }

    @Override
    public Project newProject(Project project) {
        // Creation
        try {
            int id = dbCreate(
                    "INSERT INTO PROJECTS(NAME, DESCRIPTION, DISABLED, CREATION, CREATOR) VALUES (:name, :description, :disabled, :creation, :creator)",
                    params("name", project.getName())
                            .addValue("description", project.getDescription())
                            .addValue("disabled", project.isDisabled())
                            .addValue("creation", dateTimeForDB(project.getSignature().getTime()))
                            .addValue("creator", project.getSignature().getUser().getName())
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
    public Optional<Project> getProjectByName(String project) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT * FROM PROJECTS WHERE NAME = :name",
                        params("name", project),
                        (rs, rowNum) -> toProject(rs)
                )
        );
    }

    @Override
    public void saveProject(Project project) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE PROJECTS SET NAME = :name, DESCRIPTION = :description, DISABLED = :disabled WHERE ID = :id",
                params("name", project.getName())
                        .addValue("description", project.getDescription())
                        .addValue("disabled", project.isDisabled())
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
    public Optional<Branch> getBranchByName(String project, String branch) {
        return getProjectByName(project)
                .map(p -> getFirstItem(
                        "SELECT * FROM BRANCHES WHERE PROJECTID = :project AND NAME = :name",
                        params("name", branch).addValue("project", p.id()),
                        (rs, rowNum) -> toBranch(rs, id -> p)
                ));
    }

    @Override
    public List<Branch> getBranchesForProject(ID projectId) {
        Project project = getProject(projectId);
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM BRANCHES WHERE PROJECTID = :projectId ORDER BY ID DESC",
                params("projectId", projectId.getValue()),
                (rs, rowNum) -> toBranch(rs, id -> project)
        );
    }

    @Override
    public Branch newBranch(Branch branch) {
        // Creation
        try {
            int id = dbCreate(
                    "INSERT INTO BRANCHES(PROJECTID, NAME, DESCRIPTION, DISABLED, CREATION, CREATOR) VALUES (:projectId, :name, :description, :disabled, :creation, :creator)",
                    params("name", branch.getName())
                            .addValue("description", branch.getDescription())
                            .addValue("disabled", branch.isDisabled())
                            .addValue("projectId", branch.getProject().id())
                            .addValue("creation", dateTimeForDB(branch.getSignature().getTime()))
                            .addValue("creator", branch.getSignature().getUser().getName())
            );
            // Returns with ID
            return branch.withId(id(id));
        } catch (DuplicateKeyException ex) {
            throw new BranchNameAlreadyDefinedException(branch.getName());
        }
    }

    @Override
    public void saveBranch(Branch branch) {
        // Update
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE BRANCHES SET NAME = :name, DESCRIPTION = :description, DISABLED = :disabled WHERE ID = :id",
                    params("name", branch.getName())
                            .addValue("description", branch.getDescription())
                            .addValue("disabled", branch.isDisabled())
                            .addValue("id", branch.id())
            );
        } catch (DuplicateKeyException ex) {
            throw new BranchNameAlreadyDefinedException(branch.getName());
        }
    }

    @Override
    public Ack deleteBranch(ID branchId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM BRANCHES WHERE ID = :id",
                        params("id", branchId.getValue())
                )
        );
    }

    @Override
    public void builds(Branch branch, Predicate<Build> buildPredicate, BuildSortDirection sortDirection) {
        String order = sortDirection == BuildSortDirection.FROM_NEWEST ? "DESC" : "ASC";
        getNamedParameterJdbcTemplate().execute(
                "SELECT * FROM BUILDS WHERE BRANCHID = :branchId ORDER BY ID " + order,
                params("branchId", branch.id()),
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    boolean goingOn = true;
                    while (rs.next() && goingOn) {
                        // Gets the builds
                        Build build = toBuild(
                                rs,
                                id -> branch
                        );
                        // Dealing with this build
                        goingOn = buildPredicate.test(build);
                    }
                    return null;
                }
        );
    }

    @Override
    public void builds(Project project, Predicate<Build> buildPredicate) {
        getNamedParameterJdbcTemplate().execute(
                "SELECT B.* FROM BUILDS B INNER JOIN BRANCHES R ON R.ID = B.BRANCHID AND R.PROJECTID = :projectId ORDER BY B.ID DESC",
                params("projectId", project.id()),
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    boolean goingOn = true;
                    while (rs.next() && goingOn) {
                        // Gets the builds
                        Build build = toBuild(
                                rs,
                                this::getBranch
                        );
                        // Dealing with this build
                        goingOn = buildPredicate.test(build);
                    }
                    return null;
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

    @Override
    public Ack deleteBuild(ID buildId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM BUILDS WHERE ID = :id",
                        params("id", buildId.getValue())
                )
        );
    }

    @Override
    public void addBuildLink(ID fromBuildId, ID toBuildId) {
        deleteBuildLink(fromBuildId, toBuildId);
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO BUILD_LINKS(BUILDID, TARGETBUILDID) VALUES (:fromBuildId, :toBuildId)",
                params("fromBuildId", fromBuildId.get()).addValue("toBuildId", toBuildId.get())
        );
    }

    @Override
    public void deleteBuildLink(ID fromBuildId, ID toBuildId) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM BUILD_LINKS WHERE BUILDID = :fromBuildId AND TARGETBUILDID = :toBuildId",
                params("fromBuildId", fromBuildId.get()).addValue("toBuildId", toBuildId.get())
        );
    }

    @Override
    public List<Build> getBuildLinksFrom(ID buildId) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT T.* FROM BUILDS T " +
                        "INNER JOIN BUILD_LINKS BL ON BL.TARGETBUILDID = T.ID " +
                        "WHERE BL.BUILDID = :buildId",
                params("buildId", buildId.get()),
                (rs, num) -> toBuild(rs, this::getBranch)
        );
    }

    @Override
    public List<Build> getBuildLinksTo(ID buildId) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT F.* FROM BUILDS F " +
                        "INNER JOIN BUILD_LINKS BL ON BL.BUILDID = F.ID " +
                        "WHERE BL.TARGETBUILDID = :buildId " +
                        "ORDER BY F.ID DESC",
                params("buildId", buildId.get()),
                (rs, num) -> toBuild(rs, this::getBranch)
        );
    }

    @Override
    public List<Build> searchBuildsLinkedTo(String projectName, String buildPattern) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT F.* FROM BUILDS F " +
                        "INNER JOIN BUILD_LINKS BL ON BL.BUILDID = F.ID " +
                        "INNER JOIN BUILDS T ON BL.TARGETBUILDID = T.ID " +
                        "INNER JOIN BRANCHES BR ON BR.ID = T.BRANCHID " +
                        "INNER JOIN PROJECTS P ON P.ID = BR.PROJECTID " +
                        "WHERE T.NAME LIKE :buildNamePattern AND P.NAME = :projectName " +
                        "ORDER BY F.ID DESC",
                params("buildNamePattern", expandBuildPattern(buildPattern)).addValue("projectName", projectName),
                (rs, num) -> toBuild(rs, this::getBranch)
        );
    }

    @Override
    public boolean isLinkedFrom(ID id, String project, String buildPattern) {
        return getOptional(
                "SELECT BL.BUILDID FROM BUILD_LINKS BL " +
                        "INNER JOIN BUILDS F ON BL.BUILDID = F.ID " +
                        "INNER JOIN BRANCHES BR ON BR.ID = F.BRANCHID " +
                        "INNER JOIN PROJECTS P ON P.ID = BR.PROJECTID " +
                        "WHERE BL.TARGETBUILDID = :buildId AND F.NAME LIKE :buildNamePattern AND P.NAME = :projectName " +
                        "LIMIT 1",
                params("buildId", id.get())
                        .addValue("buildNamePattern", expandBuildPattern(buildPattern))
                        .addValue("projectName", project),
                Integer.class
        ).isPresent();
    }

    private String expandBuildPattern(String buildPattern) {
        if (StringUtils.isBlank(buildPattern)) {
            return "%";
        } else {
            return StringUtils.replace(buildPattern, "*", "%");
        }
    }

    @Override
    public boolean isLinkedTo(ID id, String project, String buildPattern) {
        return getOptional(
                "SELECT BL.TARGETBUILDID FROM BUILD_LINKS BL " +
                        "INNER JOIN BUILDS T ON BL.TARGETBUILDID = T.ID " +
                        "INNER JOIN BRANCHES BR ON BR.ID = T.BRANCHID " +
                        "INNER JOIN PROJECTS P ON P.ID = BR.PROJECTID " +
                        "WHERE BL.BUILDID = :buildId AND T.NAME LIKE :buildNamePattern AND P.NAME = :projectName " +
                        "LIMIT 1",
                params("buildId", id.get())
                        .addValue("buildNamePattern", expandBuildPattern(buildPattern))
                        .addValue("projectName", project),
                Integer.class
        ).isPresent();
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
                    "UPDATE BUILDS SET NAME = :name, DESCRIPTION = :description, CREATION = :creation, CREATOR = :creator WHERE ID = :id",
                    params("name", build.getName())
                            .addValue("description", build.getDescription())
                            .addValue("creation", dateTimeForDB(build.getSignature().getTime()))
                            .addValue("creator", build.getSignature().getUser().getName())
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
    public Optional<Build> getBuildByName(String project, String branch, String build) {
        return getBranchByName(project, branch).map(b -> getFirstItem(
                "SELECT * FROM BUILDS WHERE NAME = :name AND BRANCHID = :branchId",
                params("name", build).addValue("branchId", b.id()),
                (rs, rowNum) -> toBuild(rs, this::getBranch)
        ));
    }

    @Override
    public Optional<Build> findBuildAfterUsingNumericForm(ID branchId, String buildName) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT * FROM (SELECT * FROM BUILDS WHERE BRANCHID = :branch AND NAME REGEXP '[0-9]+') " +
                                "WHERE CONVERT(NAME,INT) >= CONVERT(:name,INT) ORDER BY CONVERT(NAME,INT) " +
                                "LIMIT 1",
                        params("branch", branchId.getValue()).addValue("name", buildName),
                        (rs, rowNum) -> toBuild(rs, this::getBranch)
                )
        );
    }

    @Override
    public int getBuildCount(Branch branch) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT COUNT(ID) FROM BUILDS WHERE BRANCHID = :branchId",
                params("branchId", branch.id()),
                Integer.class
        );
    }

    @Override
    public Optional<Build> getPreviousBuild(Build build) {
        return getOptional(
                "SELECT * FROM BUILDS WHERE BRANCHID = :branch AND ID < :id ORDER BY ID DESC LIMIT 1",
                params("branch", build.getBranch().id()).addValue("id", build.id()),
                (rs, rowNum) -> toBuild(rs, this::getBranch)
        );
    }

    @Override
    public Optional<Build> getNextBuild(Build build) {
        return getOptional(
                "SELECT * FROM BUILDS WHERE BRANCHID = :branch AND ID > :id ORDER BY ID ASC LIMIT 1",
                params("branch", build.getBranch().id()).addValue("id", build.id()),
                (rs, rowNum) -> toBuild(rs, this::getBranch)
        );
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
                    "INSERT INTO PROMOTION_LEVELS(BRANCHID, NAME, DESCRIPTION, ORDERNB, CREATION, CREATOR) VALUES (:branchId, :name, :description, :orderNb, :creation, :creator)",
                    params("name", promotionLevel.getName())
                            .addValue("description", promotionLevel.getDescription())
                            .addValue("branchId", promotionLevel.getBranch().id())
                            .addValue("orderNb", orderNb)
                            .addValue("creation", dateTimeForDB(promotionLevel.getSignature().getTime()))
                            .addValue("creator", promotionLevel.getSignature().getUser().getName())
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
    public Optional<PromotionLevel> getPromotionLevelByName(String project, String branch, String promotionLevel) {
        return getBranchByName(project, branch)
                .flatMap(b -> getPromotionLevelByName(b, promotionLevel));
    }

    @Override
    public Optional<PromotionLevel> getPromotionLevelByName(Branch branch, String promotionLevel) {
        return getOptional(
                "SELECT * FROM PROMOTION_LEVELS WHERE BRANCHID = :branch AND NAME = :name",
                params("name", promotionLevel).addValue("branch", branch.id()),
                (rs, rowNum) -> toPromotionLevel(rs, id -> branch)
        );
    }

    @Override
    public Document getPromotionLevelImage(ID promotionLevelId) {
        return getOptional(
                "SELECT IMAGETYPE, IMAGEBYTES FROM PROMOTION_LEVELS WHERE ID = :id",
                params("id", promotionLevelId.getValue()),
                (rs, rowNum) -> toDocument(rs)
        ).orElse(Document.EMPTY);
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
    public void savePromotionLevel(PromotionLevel promotionLevel) {
        // Update
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE PROMOTION_LEVELS SET NAME = :name, DESCRIPTION = :description WHERE ID = :id",
                    params("name", promotionLevel.getName())
                            .addValue("description", promotionLevel.getDescription())
                            .addValue("id", promotionLevel.id())
            );
        } catch (DuplicateKeyException ex) {
            throw new PromotionLevelNameAlreadyDefinedException(promotionLevel.getName());
        }
    }

    @Override
    public Ack deletePromotionLevel(ID promotionLevelId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM PROMOTION_LEVELS WHERE ID = :id",
                        params("id", promotionLevelId.getValue())
                )
        );
    }

    @Override
    public void reorderPromotionLevels(ID branchId, Reordering reordering) {
        int order = 1;
        for (int id : reordering.getIds()) {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE PROMOTION_LEVELS SET ORDERNB = :order WHERE ID = :id",
                    params("id", id).addValue("order", order++)
            );
        }
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
    public Ack deletePromotionRun(ID promotionRunId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM PROMOTION_RUNS WHERE ID = :promotionRunId",
                        params("promotionRunId", promotionRunId.getValue())
                )
        );
    }

    @Override
    public List<PromotionRun> getPromotionRunsForBuild(Build build) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM PROMOTION_RUNS WHERE BUILDID = :buildId ORDER BY CREATION DESC",
                params("buildId", build.id()),
                (rs, rowNum) -> toPromotionRun(rs,
                        (id) -> build,
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
                "SELECT PR.* FROM PROMOTION_RUNS PR" +
                        " INNER JOIN BUILDS B ON B.ID = PR.BUILDID" +
                        " WHERE PROMOTIONLEVELID = :promotionLevelId" +
                        " ORDER BY B.ID DESC" +
                        " LIMIT 1",
                params("promotionLevelId", promotionLevel.id()),
                (rs, rowNum) -> toPromotionRun(rs,
                        this::getBuild,
                        (promotionLevelId) -> promotionLevel)
        );
    }

    @Override
    public Optional<PromotionRun> getLastPromotionRun(Build build, PromotionLevel promotionLevel) {
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

    @Override
    public List<PromotionRun> getPromotionRunsForBuildAndPromotionLevel(Build build, PromotionLevel promotionLevel) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM PROMOTION_RUNS WHERE BUILDID = :buildId AND PROMOTIONLEVELID = :promotionLevelId ORDER BY CREATION DESC",
                params("buildId", build.id()).addValue("promotionLevelId", promotionLevel.id()),
                (rs, rowNum) -> toPromotionRun(rs,
                        (id) -> build,
                        (id) -> promotionLevel
                )
        );
    }

    @Override
    public List<PromotionRun> getPromotionRunsForPromotionLevel(PromotionLevel promotionLevel) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM PROMOTION_RUNS WHERE PROMOTIONLEVELID = :promotionLevelId ORDER BY CREATION DESC, ID DESC",
                params("promotionLevelId", promotionLevel.id()),
                (rs, rowNum) -> toPromotionRun(rs,
                        this::getBuild,
                        (id) -> promotionLevel
                )
        );
    }

    @Override
    public Optional<PromotionRun> getEarliestPromotionRunAfterBuild(PromotionLevel promotionLevel, Build build) {
        return getOptional(
                "SELECT * FROM PROMOTION_RUNS WHERE PROMOTIONLEVELID = :promotionLevelId AND BUILDID >= :buildId ORDER BY CREATION ASC, ID ASC LIMIT 1",
                params("promotionLevelId", promotionLevel.id())
                        .addValue("buildId", build.id()),
                (rs, num) -> toPromotionRun(
                        rs,
                        this::getBuild,
                        (x) -> promotionLevel
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
        ).withId(id(rs));
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
                    "INSERT INTO VALIDATION_STAMPS(BRANCHID, NAME, DESCRIPTION, ORDERNB, CREATION, CREATOR) VALUES (:branchId, :name, :description, :orderNb, :creation, :creator)",
                    params("name", validationStamp.getName())
                            .addValue("description", validationStamp.getDescription())
                            .addValue("branchId", validationStamp.getBranch().id())
                            .addValue("orderNb", orderNb)
                            .addValue("creation", dateTimeForDB(validationStamp.getSignature().getTime()))
                            .addValue("creator", validationStamp.getSignature().getUser().getName())
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
    public Optional<ValidationStamp> getValidationStampByName(String project, String branch, String validationStamp) {
        return getBranchByName(project, branch)
                .flatMap(b -> getValidationStampByName(b, validationStamp));
    }

    @Override
    public Optional<ValidationStamp> getValidationStampByName(Branch branch, String validationStamp) {
        return getOptional(
                "SELECT * FROM VALIDATION_STAMPS WHERE NAME = :name AND BRANCHID = :branch",
                params("name", validationStamp).addValue("branch", branch.id()),
                (rs, rowNum) -> toValidationStamp(rs, id -> branch)
        );
    }

    @Override
    public Document getValidationStampImage(ID validationStampId) {
        return getOptional(
                "SELECT IMAGETYPE, IMAGEBYTES FROM VALIDATION_STAMPS WHERE ID = :id",
                params("id", validationStampId.getValue()),
                (rs, rowNum) -> toDocument(rs)
        ).orElse(Document.EMPTY);
    }

    @Override
    public void setValidationStampImage(ID validationStampId, Document document) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE VALIDATION_STAMPS SET IMAGETYPE = :type, IMAGEBYTES = :content WHERE ID = :id",
                params("id", validationStampId.getValue())
                        .addValue("type", Document.isValid(document) ? document.getType() : null)
                        .addValue("content", Document.isValid(document) ? document.getContent() : null)
        );
    }

    @Override
    public void bulkUpdateValidationStamps(ID validationStampId) {
        // Description & name
        ValidationStamp validationStamp = getValidationStamp(validationStampId);
        String description = validationStamp.getDescription();
        String name = validationStamp.getName();
        // Image
        Document image = getValidationStampImage(validationStampId);
        // Bulk update
        getNamedParameterJdbcTemplate().update(
                "UPDATE VALIDATION_STAMPS SET IMAGETYPE = :type, IMAGEBYTES = :content, DESCRIPTION = :description " +
                        "WHERE ID <> :id AND NAME = :name",
                params("id", validationStampId.getValue())
                        .addValue("name", name)
                        .addValue("description", description)
                        .addValue("type", Document.isValid(image) ? image.getType() : null)
                        .addValue("content", Document.isValid(image) ? image.getContent() : null)
        );
    }

    @Override
    public void saveValidationStamp(ValidationStamp validationStamp) {
        // Update
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE VALIDATION_STAMPS SET NAME = :name, DESCRIPTION = :description WHERE ID = :id",
                    params("name", validationStamp.getName())
                            .addValue("description", validationStamp.getDescription())
                            .addValue("id", validationStamp.id())
            );
        } catch (DuplicateKeyException ex) {
            throw new ValidationStampNameAlreadyDefinedException(validationStamp.getName());
        }
    }

    @Override
    public Ack deleteValidationStamp(ID validationStampId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM VALIDATION_STAMPS WHERE ID = :id",
                        params("id", validationStampId.getValue())
                )
        );
    }

    @Override
    public void reorderValidationStamps(ID branchId, Reordering reordering) {
        int order = 1;
        for (int id : reordering.getIds()) {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE VALIDATION_STAMPS SET ORDERNB = :order WHERE ID = :id",
                    params("id", id).addValue("order", order++)
            );
        }
    }

    @Override
    public ValidationRun newValidationRun(ValidationRun validationRun, Function<String, ValidationRunStatusID> validationRunStatusService) {

        // Validation run itself (parent)
        int id = dbCreate(
                "INSERT INTO VALIDATION_RUNS(BUILDID, VALIDATIONSTAMPID) VALUES (:buildId, :validationStampId)",
                params("buildId", validationRun.getBuild().id())
                        .addValue("validationStampId", validationRun.getValidationStamp().id())
        );

        // Statuses
        validationRun.getValidationRunStatuses()
                .forEach(validationRunStatus -> newValidationRunStatus(id, validationRunStatus));

        // Reloads the run
        return getValidationRun(ID.of(id), validationRunStatusService);
    }

    @Override
    public ValidationRun getValidationRun(ID validationRunId, Function<String, ValidationRunStatusID> validationRunStatusService) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM VALIDATION_RUNS WHERE ID = :id",
                params("id", validationRunId.getValue()),
                (rs, rowNum) -> toValidationRun(
                        rs,
                        this::getBuild,
                        this::getValidationStamp,
                        validationRunStatusService
                )
        );
    }

    @Override
    public List<ValidationRun> getValidationRunsForBuild(Build build, Function<String, ValidationRunStatusID> validationRunStatusService) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_RUNS WHERE BUILDID = :buildId",
                params("buildId", build.id()),
                (rs, rowNum) -> toValidationRun(
                        rs,
                        id -> build,
                        this::getValidationStamp,
                        validationRunStatusService
                )
        );
    }

    @Override
    public List<ValidationRun> getValidationRunsForBuildAndValidationStamp(Build build, ValidationStamp validationStamp, Function<String, ValidationRunStatusID> validationRunStatusService) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_RUNS WHERE BUILDID = :buildId AND VALIDATIONSTAMPID = :validationStampId ORDER BY ID DESC",
                params("buildId", build.id()).addValue("validationStampId", validationStamp.id()),
                (rs, rowNum) -> toValidationRun(
                        rs,
                        id -> build,
                        id -> validationStamp,
                        validationRunStatusService
                )
        );
    }

    @Override
    public List<ValidationRun> getValidationRunsForValidationStamp(ValidationStamp validationStamp, int offset, int count, Function<String, ValidationRunStatusID> validationRunStatusService) {
        return getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_RUNS WHERE VALIDATIONSTAMPID = :validationStampId ORDER BY BUILDID DESC, ID DESC LIMIT :limit OFFSET :offset",
                params("validationStampId", validationStamp.id())
                        .addValue("limit", count)
                        .addValue("offset", offset),
                (rs, rowNum) -> toValidationRun(
                        rs,
                        this::getBuild,
                        id -> validationStamp,
                        validationRunStatusService
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

    protected ValidationRun toValidationRun(ResultSet rs,
                                            Function<ID, Build> buildSupplier,
                                            Function<ID, ValidationStamp> validationStampSupplier,
                                            Function<String, ValidationRunStatusID> validationRunStatusService) throws SQLException {
        int id = rs.getInt("id");
        // Statuses
        List<ValidationRunStatus> statuses = getNamedParameterJdbcTemplate().query(
                "SELECT * FROM VALIDATION_RUN_STATUSES WHERE VALIDATIONRUNID = :validationRunId ORDER BY CREATION DESC",
                params("validationRunId", id),
                (rs1, rowNum) -> ValidationRunStatus.of(
                        readSignature(rs1),
                        validationRunStatusService.apply(rs1.getString("validationRunStatusId")),
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
        ).withId(id(rs))
                .withSignature(readSignature(rs))
                .withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }

    protected ValidationStamp toValidationStamp(ResultSet rs, Function<ID, Branch> branchSupplier) throws SQLException {
        return ValidationStamp.of(
                branchSupplier.apply(id(rs, "branchId")),
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        ).withId(id(rs))
                .withSignature(readSignature(rs))
                .withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }

    protected Branch toBranch(ResultSet rs, Function<ID, Project> projectSupplier) throws SQLException {
        ID projectId = id(rs, "projectId");
        ID branchId = id(rs);
        return Branch.of(
                projectSupplier.apply(projectId),
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        )
                .withId(branchId)
                .withSignature(readSignature(rs))
                .withType(getBranchType(branchId))
                .withDisabled(rs.getBoolean("disabled"));
    }

    private BranchType getBranchType(ID branchId) {
        if (branchTemplateRepository.isTemplateDefinition(branchId)) {
            return BranchType.TEMPLATE_DEFINITION;
        } else if (branchTemplateRepository.isTemplateInstance(branchId)) {
            return BranchType.TEMPLATE_INSTANCE;
        } else {
            return BranchType.CLASSIC;
        }
    }

    protected Project toProject(ResultSet rs) throws SQLException {
        return Project.of(new NameDescription(
                rs.getString("name"),
                rs.getString("description")
        ))
                .withId(id(rs.getInt("id")))
                .withSignature(readSignature(rs))
                .withDisabled(rs.getBoolean("disabled"));
    }

}
