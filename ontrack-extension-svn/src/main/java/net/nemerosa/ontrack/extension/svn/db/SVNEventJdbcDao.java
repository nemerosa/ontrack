package net.nemerosa.ontrack.extension.svn.db;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
}
