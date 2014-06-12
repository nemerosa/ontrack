package net.nemerosa.ontrack.extension.svn.db;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SVNRevisionJdbcDao extends AbstractJdbcRepository implements SVNRevisionDao {

    @Autowired
    public SVNRevisionJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getLast(int repositoryId) {
        Long value = getNamedParameterJdbcTemplate().queryForObject(
                "SELECT MAX(REVISION) FROM EXT_SVN_REVISION WHERE REPOSITORY = :repositoryId",
                params("repositoryId", repositoryId),
                Long.class);
        return value != null ? value : 0L;
    }
}
