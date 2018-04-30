package net.nemerosa.ontrack.repository.support

import java.sql.ResultSet

fun ResultSet.getNullableInt(column: String): Int? {
    val value = getInt(column)
    return if (wasNull()) {
        null
    } else {
        value
    }
}
