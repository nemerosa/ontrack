package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ValidationStamp

interface ValidationStampRepository {

    fun findByToken(token: String): List<ValidationStamp>

}