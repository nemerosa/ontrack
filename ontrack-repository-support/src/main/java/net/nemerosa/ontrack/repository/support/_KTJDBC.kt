package net.nemerosa.ontrack.repository.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.Time
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.time.LocalDateTime

fun createSQL(tables: List<String>, criteria: List<String>): String {
    val sqlTables = tables.joinToString(" ")
    return if (criteria.isNotEmpty()) {
        val sqlCriteria = criteria.joinToString(" AND ")
        "$sqlTables WHERE $sqlCriteria"
    } else {
        sqlTables
    }
}

fun ResultSet.getNullableInt(column: String): Int? {
    val value = getInt(column)
    return if (wasNull()) {
        null
    } else {
        value
    }
}

fun ResultSet.getDocumentWithType(bytesColumn: String, type: String): Document {
    val bytes: ByteArray? = getBytes(bytesColumn)
    return if (bytes != null && bytes.isNotEmpty()) {
        Document(
            type = type,
            content = bytes,
        )
    } else {
        Document.EMPTY
    }
}

fun ResultSet.readLocalDateTime(column: String): LocalDateTime? {
    val value = getString(column)
    return Time.fromStorage(value)
}

fun ResultSet.readLocalDateTimeNotNull(column: String): LocalDateTime {
    return readLocalDateTime(column) ?: error("No $column value in result set")
}

fun <T> NamedParameterJdbcTemplate.queryForObjectOrNull(
    sql: String,
    params: Map<String, *>,
    rowMapper: RowMapper<T>
): T? = try {
    @Suppress("SqlSourceToSinkFlow")
    queryForObject(sql, params, rowMapper)
} catch (e: EmptyResultDataAccessException) {
    null
}
