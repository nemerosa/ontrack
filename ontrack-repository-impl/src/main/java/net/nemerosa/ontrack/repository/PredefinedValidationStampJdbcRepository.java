package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.PredefinedValidationStampNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
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
public class PredefinedValidationStampJdbcRepository extends AbstractJdbcRepository implements PredefinedValidationStampRepository {

    private final ValidationDataTypeConfigRepository validationDataTypeConfigRepository;

    @Autowired
    public PredefinedValidationStampJdbcRepository(
            DataSource dataSource,
            ValidationDataTypeConfigRepository validationDataTypeConfigRepository
    ) {
        super(dataSource);
        this.validationDataTypeConfigRepository = validationDataTypeConfigRepository;
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
        try {
            return ID.of(
                    dbCreate(
                            "INSERT INTO PREDEFINED_VALIDATION_STAMPS(NAME, DESCRIPTION, DATA_TYPE_ID, DATA_TYPE_CONFIG) VALUES (:name, :description, :dataTypeId, :dataTypeConfig)",
                            params("name", stamp.getName())
                                    .addValue("description", stamp.getDescription())
                                    .addValue("dataTypeId", stamp.getDataType() != null ? stamp.getDataType().getDescriptor().getId() : null)
                                    .addValue("dataTypeConfig", stamp.getDataType() != null ? writeJson(stamp.getDataType().getConfig()) : null)
                    )
            );
        } catch (DuplicateKeyException ex) {
            throw new PredefinedValidationStampNameAlreadyDefinedException(stamp.getName());
        }
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

    @Override
    public void savePredefinedValidationStamp(PredefinedValidationStamp validationStamp) {
        // Update
        try {
            getNamedParameterJdbcTemplate().update(
                    "UPDATE PREDEFINED_VALIDATION_STAMPS SET NAME = :name, DESCRIPTION = :description, DATA_TYPE_ID = :dataTypeId, DATA_TYPE_CONFIG = :dataTypeConfig WHERE ID = :id",
                    params("name", validationStamp.getName())
                            .addValue("description", validationStamp.getDescription())
                            .addValue("id", validationStamp.id())
                            .addValue("dataTypeId", validationStamp.getDataType() != null ? validationStamp.getDataType().getDescriptor().getId() : null)
                            .addValue("dataTypeConfig", validationStamp.getDataType() != null ? writeJson(validationStamp.getDataType().getConfig()) : null)
            );
        } catch (DuplicateKeyException ex) {
            throw new PredefinedValidationStampNameAlreadyDefinedException(validationStamp.getName());
        }
    }

    @Override
    public Ack deletePredefinedValidationStamp(ID predefinedValidationStampId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        "DELETE FROM PREDEFINED_VALIDATION_STAMPS WHERE ID = :id",
                        params("id", predefinedValidationStampId.get())
                )
        );
    }

    @Override
    public void setPredefinedValidationStampImage(ID predefinedValidationStampId, Document document) {
        getNamedParameterJdbcTemplate().update(
                "UPDATE PREDEFINED_VALIDATION_STAMPS SET IMAGETYPE = :type, IMAGEBYTES = :content WHERE ID = :id",
                params("id", predefinedValidationStampId.getValue())
                        .addValue("type", Document.isValid(document) ? document.getType() : null)
                        .addValue("content", Document.isValid(document) ? document.getContent() : null)
        );
    }

    protected PredefinedValidationStamp toPredefinedValidationStamp(ResultSet rs) throws SQLException {
        return PredefinedValidationStamp.of(
                new NameDescription(
                        rs.getString("name"),
                        rs.getString("description")
                )
        )
                .withId(id(rs))
                .withDataType(validationDataTypeConfigRepository.readValidationDataTypeConfig(rs))
                .withImage(StringUtils.isNotBlank(rs.getString("imagetype")));
    }
}
