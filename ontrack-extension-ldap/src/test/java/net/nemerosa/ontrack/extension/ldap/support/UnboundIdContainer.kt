package net.nemerosa.ontrack.extension.ldap.support

import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig
import com.unboundid.ldap.listener.InMemoryListenerConfig
import com.unboundid.ldap.sdk.DN
import com.unboundid.ldap.sdk.Entry
import com.unboundid.ldap.sdk.LDAPException
import com.unboundid.ldif.LDIFReader
import org.springframework.beans.BeansException
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.Lifecycle
import org.springframework.util.StringUtils


/**
 * Copied from Spring version to add the possibility to remove the schema.
 */
class UnboundIdContainer(private val defaultPartitionSuffix: String, private val ldif: String) : InitializingBean, DisposableBean, Lifecycle, ApplicationContextAware {
    private var directoryServer: InMemoryDirectoryServer? = null
    private var port = 53389
    private var context: ApplicationContext? = null
    private var running = false

    override fun destroy() {
        stop()
    }

    override fun afterPropertiesSet() {
        start()
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    override fun start() {
        if (isRunning) {
            return
        }
        try {
            val config = InMemoryDirectoryServerConfig(defaultPartitionSuffix)
            config.addAdditionalBindCredentials("uid=admin,ou=system", "secret")
            config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("LDAP", port))
            config.setEnforceSingleStructuralObjectClass(false)
            config.setEnforceAttributeSyntaxCompliance(true)
            config.schema = null
            val dn = DN(defaultPartitionSuffix)
            val entry = Entry(dn)
            entry.addAttribute("objectClass", "top", "domain", "extensibleObject")
            entry.addAttribute("dc", dn.rdn.attributeValues[0])
            val directoryServer = InMemoryDirectoryServer(config)
            directoryServer.add(entry)
            importLdif(directoryServer)
            directoryServer.startListening()
            port = directoryServer.listenPort
            this.directoryServer = directoryServer
            running = true
        } catch (ex: LDAPException) {
            throw RuntimeException("Server startup failed", ex)
        }
    }

    private fun importLdif(directoryServer: InMemoryDirectoryServer) {
        if (StringUtils.hasText(ldif)) {
            try {
                val resources = context!!.getResources(ldif)
                if (resources.isNotEmpty() && resources[0].exists()) {
                    resources[0].inputStream.use { inputStream -> directoryServer.importFromLDIF(false, LDIFReader(inputStream)) }
                }
            } catch (ex: Exception) {
                throw IllegalStateException("Unable to load LDIF $ldif", ex)
            }
        }
    }

    override fun stop() {
        directoryServer!!.shutDown(true)
    }

    override fun isRunning(): Boolean {
        return running
    }

}
