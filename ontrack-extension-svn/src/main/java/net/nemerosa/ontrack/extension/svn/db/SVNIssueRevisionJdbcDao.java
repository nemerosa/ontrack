package net.nemerosa.ontrack.extension.svn.db;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SVNIssueRevisionJdbcDao extends AbstractJdbcRepository implements SVNIssueRevisionDao {

    private static final int ISSUE_KEY_MAX_LENGTH = 20;
    private final Logger logger = LoggerFactory.getLogger(SVNIssueRevisionDao.class);

    @Autowired
    public SVNIssueRevisionJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void link(int repositoryId, long revision, String key) {
        if (StringUtils.isBlank(key)) {
            logger.warn("Cannot insert a null or blank key (revision {})", revision);
        } else if (key.length() > ISSUE_KEY_MAX_LENGTH) {
            logger.warn("Cannot insert a key longer than {} characters: {} for revision {}", ISSUE_KEY_MAX_LENGTH, key, revision);
        } else {
            getNamedParameterJdbcTemplate().update(
                    "INSERT INTO EXT_SVN_REVISION_ISSUE (REPOSITORY, REVISION, ISSUE) VALUES (:repository, :revision, :key)",
                    params("revision", revision).addValue("key", key).addValue("repository", repositoryId));
        }
    }
}
