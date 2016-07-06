package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ProjectFavouriteJdbcRepository extends AbstractJdbcRepository implements ProjectFavouriteRepository {

    @Autowired
    public ProjectFavouriteJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean isProjectFavourite(int accountId, int projectId) {
        return getOptional(
                "SELECT ID FROM PROJECT_FAVOURITES WHERE ACCOUNTID = :account AND PROJECTID = :project",
                params("account", accountId).addValue("project", projectId),
                Integer.class
        ).isPresent();
    }

    @Override
    public void setProjectFavourite(int accountId, int projectId, boolean favourite) {
        if (favourite) {
            if (!isProjectFavourite(accountId, projectId)) {
                getNamedParameterJdbcTemplate().update(
                        "INSERT INTO PROJECT_FAVOURITES(ACCOUNTID, PROJECTID) VALUES (:account, :project)",
                        params("account", accountId).addValue("project", projectId)
                );
            }
        } else {
            getNamedParameterJdbcTemplate().update(
                    "DELETE FROM PROJECT_FAVOURITES WHERE ACCOUNTID = :account AND PROJECTID = :project",
                    params("account", accountId).addValue("project", projectId)
            );
        }
    }
}
