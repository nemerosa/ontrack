package net.nemerosa.ontrack.repository.support

import net.nemerosa.ontrack.common.Document
import java.sql.ResultSet

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
