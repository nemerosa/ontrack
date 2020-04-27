package net.nemerosa.ontrack.extension.ldap.support

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LDAPServer(
        private val ldifPath: String = "/users.ldif",
        private val partitionSuffix: String = "dc=nemerosa,dc=net"
) {

    private val logger: Logger = LoggerFactory.getLogger(LDAPServer::class.java)

    fun start() {
        val ldif = LDAPServer::class.java.getResourceAsStream(ldifPath).reader().readText()
        val ldapContainer = UnboundIdContainer(partitionSuffix, ldif)
        logger.info("Starting on port ${ldapContainer.port}...")
        ldapContainer.start()
    }
}

fun main() {
    LDAPServer().start()
}