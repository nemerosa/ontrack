package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PredefinedValidationStampJdbcRepository extends AbstractJdbcRepository implements PredefinedValidationStampRepository {

    @Autowired
    public PredefinedValidationStampJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<PredefinedValidationStamp> getPredefinedValidationStamps() {
        return getJdbcTemplate().query(
                "SELECT * FROM PREDEFINED_VALIDATION_STAMPS ORDER BY NAME",
                (rs, rowNum) -> toPredefinedValidationStamp(rs)
        );
    }

    @Override
    public ID newPredefinedValidationStamp(PredefinedValidationStamp stamp) {
        return ID.of(
                dbCreate(
                        "INSERT INTO PREDEFINED_VALIDATION_STAMPS(NAME, DESCRIPTION) VALUES (:name, :description)",
                        params("name", stamp.getName())
                                .addValue("description", stamp.getDescription())
                )
        );
    }

    @Override
    public PredefinedValidationStamp getPredefinedValidationStamp(ID id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM PREDEFINED_VALIDATION_STAMPS WHERE ID = :id",
                params("id", id.get()),
                (rs, rowNum) -> toPredefinedValidationStamp(rs)
        );
    }

    @Override
    public Optional<PredefinedValidationStamp> findPredefinedValidationStampByName(String name) {
        return getOptional(
                "SELECT * FROM PREDEFINED_VALIDATION_STAMPS WHERE NAME = :name",
                params("name", name),
                (rs, rowNum) -> toPredefinedValidationStamp(rs)
        );
    }

    @Override
    public Document getPredefinedValidationStampImage(ID id) {
        return getOptional(
                "SELECT IMAGETYPE, IMAGEBYTES FROM PREDEFINED_VALIDATION_STAMPS WHERE ID = :id",
                params("id", id.getValue()),
                (rs, rowNum) -> toDocument(rs)
        ).orElse(Document.EMPTY);
    }

    protected PredefinedValidationStamp toPredefinedValidationStamp(ResultSet rs) throws SQLException {
        return PredefinedValidationStamp.of(
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        ).withId(id(rs)).withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }
}
