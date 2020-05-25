package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.labels.LabelCategoryNameAlreadyExistException
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelIdNotFoundException
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class LabelJdbcRepository(
        dataSource: DataSource
) : AbstractJdbcRepository(dataSource), LabelRepository {

    override fun findLabelsByProvider(providerId: String): List<LabelRecord> {
        return namedParameterJdbcTemplate.query(
                "SELECT * FROM LABEL WHERE COMPUTED_BY = :computedBy",
                params("computedBy", providerId)
        ) { rs, _ -> rsConversion(rs) }
    }

    override fun findLabels(category: String?, name: String?): List<LabelRecord> {
        val sql: String
        val params: MapSqlParameterSource
        if (category == null) {
            if (name == null) {
                sql = "SELECT * FROM LABEL ORDER BY CATEGORY, NAME"
                params = MapSqlParameterSource()
            } else {
                sql = "SELECT * FROM LABEL WHERE NAME = :name ORDER BY CATEGORY, NAME"
                params = params("name", name)
            }
        } else if (name == null) {
            sql = "SELECT * FROM LABEL WHERE CATEGORY = :category ORDER BY CATEGORY, NAME"
            params = params("category", category)
        } else {
            sql = "SELECT * FROM LABEL WHERE CATEGORY = :category AND NAME = :name ORDER BY CATEGORY, NAME"
            params = params("category", category).addValue("name", name)
        }
        return namedParameterJdbcTemplate.query(
                sql,
                params
        ) { rs, _ -> rsConversion(rs) }
    }

    override fun findLabelByCategoryAndNameAndProvider(category: String?, name: String, providerId: String): LabelRecord? {
        return getFirstItem(
                """SELECT *
                   FROM LABEL
                   WHERE COMPUTED_BY = :computedBy
                   AND CATEGORY = :category
                   AND NAME = :name
                   """,
                params("computedBy", providerId)
                        .addValue("category", category)
                        .addValue("name", name)
        ) { rs, _ -> rsConversion(rs) }
    }

    private fun findLabelByCategoryAndName(category: String?, name: String): LabelRecord? {
        return getFirstItem(
                """SELECT *
                   FROM LABEL
                   WHERE CATEGORY = :category
                   AND NAME = :name
                   """,
                params("category", category)
                        .addValue("name", name)
        ) { rs, _ -> rsConversion(rs) }
    }

    override fun newLabel(form: LabelForm): LabelRecord =
            newLabel(form, null)

    override fun overrideLabel(form: LabelForm, providerId: String): LabelRecord {
        val record = findLabelByCategoryAndName(
                form.category,
                form.name
        )
        return if (record != null) {
            updateAndOverrideLabel(record.id, form, providerId)
        } else {
            newLabel(form, providerId)
        }
    }

    private fun newLabel(form: LabelForm, computedBy: String?): LabelRecord {
        try {
            val id = dbCreate("""
                        INSERT INTO LABEL(category, name, description, color, computed_by)
                        VALUES (:category, :name, :description, :color, :computedBy)
                    """,
                    params("computedBy", computedBy)
                            .addValue("category", form.category)
                            .addValue("name", form.name)
                            .addValue("description", form.description)
                            .addValue("color", form.color)
            )
            return LabelRecord(
                    id = id,
                    category = form.category,
                    name = form.name,
                    description = form.description,
                    color = form.color,
                    computedBy = computedBy
            )
        } catch (_: DuplicateKeyException) {
            throw LabelCategoryNameAlreadyExistException(form.category, form.name)
        }
    }

    override fun updateAndOverrideLabel(labelId: Int, form: LabelForm, providerId: String): LabelRecord =
            updateLabel(labelId, form, providerId)

    override fun updateLabel(labelId: Int, form: LabelForm): LabelRecord =
            updateLabel(labelId, form, null)

    private fun updateLabel(labelId: Int, form: LabelForm, providerId: String?): LabelRecord {
        try {
            namedParameterJdbcTemplate.update("""
                        UPDATE LABEL
                        SET category = :category,
                            name = :name,
                            description = :description,
                            color = :color,
                            computed_by = :providerId
                        WHERE id = :id
                    """,
                    params("category", form.category)
                            .addValue("name", form.name)
                            .addValue("description", form.description)
                            .addValue("color", form.color)
                            .addValue("id", labelId)
                            .addValue("providerId", providerId)
            )
            return getLabel(labelId)
        } catch (_: DuplicateKeyException) {
            throw LabelCategoryNameAlreadyExistException(form.category, form.name)
        }
    }

    override fun deleteLabel(labelId: Int): Ack {
        return Ack.one(
                namedParameterJdbcTemplate.update(
                        "DELETE FROM LABEL WHERE ID = :id",
                        params("id", labelId)
                )
        )
    }

    override fun findLabelById(labelId: Int): LabelRecord? {
        return getFirstItem(
                "SELECT * FROM LABEL WHERE ID = :id",
                params("id", labelId)
        ) { rs, _ -> rsConversion(rs) }
    }

    override fun getLabel(labelId: Int): LabelRecord {
        return findLabelById(labelId) ?: throw LabelIdNotFoundException(labelId)
    }

    override val labels: List<LabelRecord>
        get() = jdbcTemplate.query(
                "SELECT * FROM LABEL ORDER BY CATEGORY, NAME"
        ) { rs, _ -> rsConversion(rs) }

    private val rsConversion: (ResultSet) -> LabelRecord = { rs: ResultSet ->
        LabelRecord(
                id = rs.getInt("ID"),
                category = rs.getString("CATEGORY"),
                name = rs.getString("NAME"),
                description = rs.getString("DESCRIPTION"),
                color = rs.getString("COLOR"),
                computedBy = rs.getString("COMPUTED_BY")
        )
    }
}