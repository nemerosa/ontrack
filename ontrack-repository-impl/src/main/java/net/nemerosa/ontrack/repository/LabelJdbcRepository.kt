package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.labels.LabelCategoryNameAlreadyExistException
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelIdNotFoundException
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class LabelJdbcRepository(
        dataSource: DataSource
) : AbstractJdbcRepository(dataSource), LabelRepository {

    override fun newLabel(form: LabelForm, computedBy: String?): LabelRecord {
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

    override fun updateLabel(labelId: Int, form: LabelForm): LabelRecord {
        try {
            namedParameterJdbcTemplate.update("""
                        UPDATE LABEL
                        SET category = :category,
                            name = :name,
                            description = :description,
                            color = :color
                        WHERE id = :id
                    """,
                    params("category", form.category)
                            .addValue("name", form.name)
                            .addValue("description", form.description)
                            .addValue("color", form.color)
                            .addValue("id", labelId)
            )
            return getLabel(labelId)
        } catch (_: DuplicateKeyException) {
            throw LabelCategoryNameAlreadyExistException(form.category, form.name)
        }
    }

    override fun getLabel(labelId: Int): LabelRecord {
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    "SELECT * FROM LABEL WHERE ID = :id",
                    params("id", labelId)
            ) { rs, _ -> rsConversion(rs) }
        } catch (_: EmptyResultDataAccessException) {
            throw LabelIdNotFoundException(labelId)
        }
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