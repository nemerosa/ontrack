package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.Document.Companion.isValid
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.PredefinedValidationStampNameAlreadyDefinedException
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp.Companion.of
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

@Repository
class PredefinedValidationStampJdbcRepository(
    dataSource: DataSource,
    private val validationDataTypeConfigRepository: ValidationDataTypeConfigRepository
) : AbstractJdbcRepository(dataSource), PredefinedValidationStampRepository {

    override val predefinedValidationStamps: List<PredefinedValidationStamp>
        get() = jdbcTemplate!!.query(
            "SELECT * FROM PREDEFINED_VALIDATION_STAMPS ORDER BY NAME"
        ) { rs: ResultSet, rowNum: Int -> toPredefinedValidationStamp(rs) }

    override fun findPredefinedValidationStamps(name: String): List<PredefinedValidationStamp> {
        return namedParameterJdbcTemplate!!.query(
            """
                SELECT * 
                FROM PREDEFINED_VALIDATION_STAMPS
                WHERE NAME ILIKE :name
                ORDER BY NAME
            """,
            mapOf("name" to "%$name%"),
        ) { rs: ResultSet, _: Int -> toPredefinedValidationStamp(rs) }
    }

    override fun newPredefinedValidationStamp(stamp: PredefinedValidationStamp): ID {
        try {
            return of(
                dbCreate(
                    "INSERT INTO PREDEFINED_VALIDATION_STAMPS(NAME, DESCRIPTION, DATA_TYPE_ID, DATA_TYPE_CONFIG) VALUES (:name, :description, :dataTypeId, :dataTypeConfig)",
                    params("name", stamp.name)
                        .addValue("description", Objects.toString(stamp.description, ""))
                        .addValue("dataTypeId", if (stamp.dataType != null) stamp.dataType!!.descriptor.id else null)
                        .addValue(
                            "dataTypeConfig",
                            if (stamp.dataType != null) writeJson(stamp.dataType!!.config) else null
                        )
                )
            )
        } catch (ex: DuplicateKeyException) {
            throw PredefinedValidationStampNameAlreadyDefinedException(stamp.name)
        }
    }

    override fun getPredefinedValidationStamp(id: ID): PredefinedValidationStamp {
        return namedParameterJdbcTemplate!!.queryForObject(
            "SELECT * FROM PREDEFINED_VALIDATION_STAMPS WHERE ID = :id",
            params("id", id.get())
        ) { rs: ResultSet, _: Int -> toPredefinedValidationStamp(rs) }
            ?: error("Could not find predefined validation stamp with id: $id")
    }

    override fun findPredefinedValidationStampByName(name: String): PredefinedValidationStamp? {
        return getFirstItem(
            "SELECT * FROM PREDEFINED_VALIDATION_STAMPS WHERE NAME = :name",
            params("name", name)
        ) { rs: ResultSet, _: Int -> toPredefinedValidationStamp(rs) }
    }

    override fun getPredefinedValidationStampImage(id: ID): Document {
        return getOptional(
            "SELECT IMAGETYPE, IMAGEBYTES FROM PREDEFINED_VALIDATION_STAMPS WHERE ID = :id",
            params("id", id.value)
        ) { rs: ResultSet?, _: Int -> toDocument(rs) }.orElse(Document.EMPTY)
    }

    override fun savePredefinedValidationStamp(stamp: PredefinedValidationStamp) {
        // Update
        try {
            namedParameterJdbcTemplate!!.update(
                "UPDATE PREDEFINED_VALIDATION_STAMPS SET NAME = :name, DESCRIPTION = :description, DATA_TYPE_ID = :dataTypeId, DATA_TYPE_CONFIG = :dataTypeConfig WHERE ID = :id",
                params("name", stamp.name)
                    .addValue("description", Objects.toString(stamp.description, ""))
                    .addValue("id", stamp.id())
                    .addValue(
                        "dataTypeId",
                        if (stamp.dataType != null) stamp.dataType!!.descriptor.id else null
                    )
                    .addValue(
                        "dataTypeConfig",
                        if (stamp.dataType != null) writeJson(stamp.dataType!!.config) else null
                    )
            )
        } catch (ex: DuplicateKeyException) {
            throw PredefinedValidationStampNameAlreadyDefinedException(stamp.name)
        }
    }

    override fun deletePredefinedValidationStamp(predefinedValidationStampId: ID): Ack {
        return Ack.one(
            namedParameterJdbcTemplate!!.update(
                "DELETE FROM PREDEFINED_VALIDATION_STAMPS WHERE ID = :id",
                params("id", predefinedValidationStampId.get())
            )
        )
    }

    override fun setPredefinedValidationStampImage(predefinedValidationStampId: ID, document: Document) {
        namedParameterJdbcTemplate!!.update(
            "UPDATE PREDEFINED_VALIDATION_STAMPS SET IMAGETYPE = :type, IMAGEBYTES = :content WHERE ID = :id",
            params("id", predefinedValidationStampId.value)
                .addValue("type", if (isValid(document)) document.type else null)
                .addValue("content", if (isValid(document)) document.content else null)
        )
    }

    @Throws(SQLException::class)
    protected fun toPredefinedValidationStamp(rs: ResultSet): PredefinedValidationStamp {
        return of(
            NameDescription(
                rs.getString("name"),
                rs.getString("description")
            )
        )
            .withId(id(rs))
            .withDataType(validationDataTypeConfigRepository.readValidationDataTypeConfig<Any>(rs))
            .withImage(StringUtils.isNotBlank(rs.getString("imagetype")))
    }
}
