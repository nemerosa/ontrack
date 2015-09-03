package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.PredefinedPromotionLevelNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PredefinedPromotionLevelJdbcRepository extends AbstractJdbcRepository implements PredefinedPromotionLevelRepository {

    @Autowired
    public PredefinedPromotionLevelJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<PredefinedPromotionLevel> getPredefinedPromotionLevels() {
        return getJdbcTemplate().query(
                "SELECT * FROM PREDEFINED_PROMOTION_LEVELS ORDER BY NAME",
                (rs, rowNum) -> toPredefinedPromotionLevel(rs)
        );
    }

    @Override
    public ID newPredefinedPromotionLevel(PredefinedPromotionLevel stamp) {
        try {
            return ID.of(
                    dbCreate(
                            "INSERT INTO PREDEFINED_PROMOTION_LEVELS(NAME, DESCRIPTION) VALUES (:name, :description)",
                            params("name", stamp.getName())
                                    .addValue("description", stamp.getDescription())
                    )
            );
        } catch (DuplicateKeyException ex) {
            throw new PredefinedPromotionLevelNameAlreadyDefinedException(stamp.getName());
        }
    }

    @Override
    public PredefinedPromotionLevel getPredefinedPromotionLevel(ID id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM PREDEFINED_PROMOTION_LEVELS WHERE ID = :id",
                params("id", id.get()),
                (rs, rowNum) -> toPredefinedPromotionLevel(rs)
        );
    }

    @Override
    public Optional<PredefinedPromotionLevel> findPredefinedPromotionLevelByName(String name) {
        return getOptional(
                "SELECT * FROM PREDEFINED_PROMOTION_LEVELS WHERE NAME = :name",
                params("name", name),
                (rs, rowNum) -> toPredefinedPromotionLevel(rs)
        );
    }

    @Override
    public Document getPredefinedPromotionLevelImage(ID id) {
        return getOptional(
                "SELECT IMAGETYPE, IMAGEBYTES FROM PREDEFINED_PROMOTION_LEVELS WHERE ID = :id",
                params("id", id.getValue()),
                (rs, rowNum) -> toDocument(rs)
        ).orElse(Document.EMPTY);
    }

    @Override
    public void savePredefinedPromotionLevel(PredefinedPromotionLevel validationStamp) {
        // Update
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE PREDEFINED_PROMOTION_LEVELS SET NAME = :name, DESCRIPTION = :description WHERE ID = :id",
                    params("name", validationStamp.getName())
                            .addValue("description", validationStamp.getDescription())
                            .addValue("id", validationStamp.id())
            );
        } catch (DuplicateKeyException ex) {
            throw new PredefinedPromotionLevelNameAlreadyDefinedException(validationStamp.getName());
        }
    }

    @Override
    public Ack deletePredefinedPromotionLevel(ID predefinedPromotionLevelId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM PREDEFINED_PROMOTION_LEVELS WHERE ID = :id",
                        params("id", predefinedPromotionLevelId.get())
                )
        );
    }

    @Override
    public void setPredefinedPromotionLevelImage(ID predefinedPromotionLevelId, Document document) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE PREDEFINED_PROMOTION_LEVELS SET IMAGETYPE = :type, IMAGEBYTES = :content WHERE ID = :id",
                params("id", predefinedPromotionLevelId.getValue())
                        .addValue("type", Document.isValid(document) ? document.getType() : null)
                        .addValue("content", Document.isValid(document) ? document.getContent() : null)
        );
    }

    protected PredefinedPromotionLevel toPredefinedPromotionLevel(ResultSet rs) throws SQLException {
        return PredefinedPromotionLevel.of(
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        ).withId(id(rs)).withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }
}
