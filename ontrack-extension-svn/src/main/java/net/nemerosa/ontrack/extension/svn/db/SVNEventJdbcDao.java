package net.nemerosa.ontrack.extension.svn.db;

import net.nemerosa.ontrack.extension.svn.model.SVNLocation;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Repository
public class SVNEventJdbcDao extends AbstractJdbcRepository implements SVNEventDao {

    @Autowired
    public SVNEventJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void createCopyEvent(int repositoryId, long revision, String copyFromPath, long copyFromRevision, String copyToPath) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO EXT_SVN_COPY (REPOSITORY, REVISION, COPYFROMPATH, COPYFROMREVISION, COPYTOPATH) VALUES (:repository, :revision, :copyFromPath, :copyFromRevision, :copyToPath)",
                params("revision", revision)
                        .addValue("repository", repositoryId)
                        .addValue("copyFromPath", copyFromPath)
                        .addValue("copyFromRevision", copyFromRevision)
                        .addValue("copyToPath", copyToPath)
        );
    }

    @Override
    public void createStopEvent(int repositoryId, long revision, String path) {
        getNamedParameterJdbcTemplate().update(
                "INSERT INTO EXT_SVN_STOP (REPOSITORY, REVISION, PATH) VALUES (:repository, :revision, :path)",
                params("revision", revision)
                        .addValue("repository", repositoryId)
                        .addValue("path", path));
    }

    @Override
    public TCopyEvent getLastCopyEvent(int repositoryId, String path, long revision) {
        return getFirstItem(
                "SELECT * FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYTOPATH = :path AND REVISION <= :revision ORDER BY REVISION DESC LIMIT 1",
                params("path", path).addValue("revision", revision).addValue("repository", repositoryId),
                (rs, rowNum) -> toCopyEvent(rs));
    }

    private TCopyEvent toCopyEvent(ResultSet rs) throws SQLException {
        return new TCopyEvent(
                rs.getInt("repository"),
                rs.getLong("revision"),
                rs.getString("copyFromPath"),
                rs.getLong("copyFromRevision"),
                rs.getString("copyToPath")
        );
    }

    @Override
    public SVNLocation getFirstCopyAfter(int repositoryId, SVNLocation location) {
        return getFirstItem(
                "SELECT * FROM EXT_SVN_COPY WHERE REPOSITORY = :repository AND COPYFROMPATH = :path AND COPYFROMREVISION >= :revision",
                params("path", location.getPath()).addValue("revision", location.getRevision()).addValue("repository", repositoryId),
                (rs, rowNum) -> new SVNLocation(
                        rs.getString("copyToPath"),
                        rs.getLong("revision")
                )
        );
    }

    @Override
    public List<TCopyEvent> findCopies(int repositoryId, String fromPath, String toPathPrefix, Predicate<TCopyEvent> filter) {
        return getNamedParameterJdbcTemplate().execute(
                "SELECT * FROM EXT_SVN_COPY WHERE REPOSITORY = :repositoryId " +
                        "AND COPYFROMPATH = :fromPath " +
                        "AND COPYTOPATH LIKE :toPath",
                params("repositoryId", repositoryId)
                        .addValue("fromPath", fromPath)
                        .addValue("toPath", toPathPrefix + "%"),
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    List<TCopyEvent> events = new ArrayList<>();
                    while (rs.next()) {
                        TCopyEvent event = toCopyEvent(rs);
                        if (filter.test(event)) {
                            events.add(event);
                        }
                    }
                    // List
                    return events;
                }
        );
    }
}
