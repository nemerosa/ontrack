package net.nemerosa.ontrack.extension.svn.db;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SVNRepositoryJdbcDao extends AbstractJdbcRepository implements SVNRepositoryDao {

    @Autowired
    public SVNRepositoryJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Integer findByName(String name) {
        return getFirstItem(
                "SELECT ID FROM EXT_SVN_REPOSITORY WHERE NAME = :name",
                params("name", name),
                Integer.class
        );
    }

    @Override
    public void delete(int id) {
        getNamedParameterJdbcTemplate().update(
                "DELETE FROM EXT_SVN_REPOSITORY WHERE ID = :id",
                params("id", id)
        );
    }

    @Override
    public int create(String name) {
        return dbCreate(
                "INSERT INTO EXT_SVN_REPOSITORY (NAME) VALUES (:name)",
                params("name", name)
        );
    }

    @Override
    public int getByName(String name) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT ID FROM EXT_SVN_REPOSITORY WHERE NAME = :name",
                params("name", name),
                Integer.class
        );
    }

    @Override
    public int getOrCreateByName(String name) {
        Integer id = findByName(name);
        return id != null ? id : create(name);
    }
}
