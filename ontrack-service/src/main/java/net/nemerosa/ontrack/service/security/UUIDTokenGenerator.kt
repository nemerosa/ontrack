package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.structure.TokenGenerator
import org.springframework.stereotype.Component
import java.util.*

/**
 * Generates tokens based on UUID
 */
@Component
class UUIDTokenGenerator : TokenGenerator {

    override fun generateToken(): String = UUID.randomUUID().toString()

}