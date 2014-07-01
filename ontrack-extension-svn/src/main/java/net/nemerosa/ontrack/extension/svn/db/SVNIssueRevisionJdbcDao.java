package net.nemerosa.ontrack.extension.svn.db;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<String> findIssuesByRevision(int repositoryId, long revision) {
        return getNamedParameterJdbcTemplate().queryForList(
                "SELECT ISSUE FROM EXT_SVN_REVISION_ISSUE WHERE REPOSITORY = :repository AND REVISION = :revision ORDER BY ISSUE",
                params("revision", revision).addValue("repository", repositoryId),
                String.class
        );
    }

    @Override
    public Optional<String> findIssueByKey(int repositoryId, String issueKey) {
        return Optional.ofNullable(
                getFirstItem(
                        "SELECT ISSUE FROM EXT_SVN_REVISION_ISSUE WHERE REPOSITORY = :repository AND ISSUE = :issue ORDER BY REVISION LIMIT 1",
                        params("repository", repositoryId).addValue("issue", issueKey),
                        String.class
                )
        );
    }
}
