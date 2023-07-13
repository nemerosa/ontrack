package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import java.sql.ResultSet

fun AbstractJdbcRepository.toProject(rs: ResultSet) = Project(
    id = ID.of(rs.getInt("ID")),
    name = rs.getString("name"),
    description = rs.getString("description"),
    isDisabled = rs.getBoolean("disabled"),
    signature = readSignature(rs)
)
