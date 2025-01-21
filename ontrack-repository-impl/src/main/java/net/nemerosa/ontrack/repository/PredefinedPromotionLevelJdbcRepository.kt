package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.Document.Companion.isValid
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.PredefinedPromotionLevelNameAlreadyDefinedException
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel.Companion.of
import net.nemerosa.ontrack.model.structure.Reordering
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

@Repository
class PredefinedPromotionLevelJdbcRepository(dataSource: DataSource) :
    AbstractJdbcRepository(dataSource), PredefinedPromotionLevelRepository {

    override val predefinedPromotionLevels: List<PredefinedPromotionLevel>
        get() = jdbcTemplate!!.query(
            "SELECT * FROM PREDEFINED_PROMOTION_LEVELS ORDER BY ORDERNB"
        ) { rs: ResultSet, rowNum: Int -> toPredefinedPromotionLevel(rs) }

    override fun newPredefinedPromotionLevel(stamp: PredefinedPromotionLevel): ID {
        try {
            // Order nb = max + 1
            val orderNbValue = getFirstItem(
                "SELECT MAX(ORDERNB) FROM PREDEFINED_PROMOTION_LEVELS",
                noParams(),
                Int::class.java
            )
            val orderNb = if (orderNbValue != null) orderNbValue + 1 else 0
            return of(
                dbCreate(
                    "INSERT INTO PREDEFINED_PROMOTION_LEVELS(NAME, ORDERNB, DESCRIPTION) VALUES (:name, :orderNb, :description)",
                    params("name", stamp.name)
                        .addValue("description", stamp.description)
                        .addValue("orderNb", orderNb)
                )
            )
        } catch (ex: DuplicateKeyException) {
            throw PredefinedPromotionLevelNameAlreadyDefinedException(stamp.name)
        }
    }

    override fun getPredefinedPromotionLevel(id: ID): PredefinedPromotionLevel {
        return getFirstItem(
            "SELECT * FROM PREDEFINED_PROMOTION_LEVELS WHERE ID = :id",
            mapOf("id" to id.value)
        ) { rs, _ -> toPredefinedPromotionLevel(rs) }
    }

    override fun findPredefinedPromotionLevels(name: String): List<PredefinedPromotionLevel> {
        return namedParameterJdbcTemplate!!.query(
            """
                SELECT * 
                FROM PREDEFINED_PROMOTION_LEVELS
                WHERE NAME ILIKE :name
                ORDER BY ORDERNB
            """,
            mapOf("name" to "%$name%"),
        ) { rs: ResultSet, rowNum: Int -> toPredefinedPromotionLevel(rs) }
    }

    override fun findPredefinedPromotionLevelByName(name: String): PredefinedPromotionLevel? {
        return getFirstItem(
            "SELECT * FROM PREDEFINED_PROMOTION_LEVELS WHERE NAME = :name",
            params("name", name)
        ) { rs: ResultSet, rowNum: Int -> toPredefinedPromotionLevel(rs) }
    }

    override fun getPredefinedPromotionLevelImage(id: ID): Document? {
        return getFirstItem(
            "SELECT IMAGETYPE, IMAGEBYTES FROM PREDEFINED_PROMOTION_LEVELS WHERE ID = :id",
            params("id", id.value)
        ) { rs: ResultSet?, rowNum: Int -> toDocument(rs) } ?: Document.EMPTY
    }

    override fun savePredefinedPromotionLevel(predefinedPromotionLevel: PredefinedPromotionLevel) {
        // Update
        try {
            namedParameterJdbcTemplate!!.update(
                "UPDATE PREDEFINED_PROMOTION_LEVELS SET NAME = :name, DESCRIPTION = :description WHERE ID = :id",
                params("name", predefinedPromotionLevel.name)
                    .addValue("description", predefinedPromotionLevel.description)
                    .addValue("id", predefinedPromotionLevel.id())
            )
        } catch (ex: DuplicateKeyException) {
            throw PredefinedPromotionLevelNameAlreadyDefinedException(predefinedPromotionLevel.name)
        }
    }

    override fun deletePredefinedPromotionLevel(predefinedPromotionLevelId: ID): Ack {
        return Ack.one(
            namedParameterJdbcTemplate!!.update(
                "DELETE FROM PREDEFINED_PROMOTION_LEVELS WHERE ID = :id",
                params("id", predefinedPromotionLevelId.get())
            )
        )
    }

    override fun setPredefinedPromotionLevelImage(predefinedPromotionLevelId: ID, document: Document) {
        namedParameterJdbcTemplate!!.update(
            "UPDATE PREDEFINED_PROMOTION_LEVELS SET IMAGETYPE = :type, IMAGEBYTES = :content WHERE ID = :id",
            params("id", predefinedPromotionLevelId.value)
                .addValue("type", if (isValid(document)) document.type else null)
                .addValue("content", if (isValid(document)) document.content else null)
        )
    }

    override fun reorderPredefinedPromotionLevels(reordering: Reordering) {
        var order = 1
        for (id in reordering.ids) {
            namedParameterJdbcTemplate!!.update(
                "UPDATE PREDEFINED_PROMOTION_LEVELS SET ORDERNB = :order WHERE ID = :id",
                params("id", id).addValue("order", order++)
            )
        }
    }

    @Throws(SQLException::class)
    protected fun toPredefinedPromotionLevel(rs: ResultSet): PredefinedPromotionLevel {
        return of(
            NameDescription(
                rs.getString("name"),
                rs.getString("description")
            )
        ).withId(id(rs)).withImage(StringUtils.isNotBlank(rs.getString("imagetype")))
    }
}
