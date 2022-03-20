package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AccountGroupMappingsAdminContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var accountGroupMappingService: AccountGroupMappingService

    @Test
    fun `Creation of new mappings`() {
        asAdmin {
            val name = uid("g")
            casc("""
                ontrack:
                    admin:
                        groups:
                            - name: $name
                              description: My group
                        group-mappings:
                            - provider: test
                              providerKey: ""
                              providerGroup: "test-group"
                              group: $name
            """.trimIndent())
            val mapping = accountService.findAccountGroupByName(name)?.let {
                accountGroupMappingService.getMappingsForGroup(it)
            }?.firstOrNull()
            assertNotNull(mapping, "Mapping created") {
                assertEquals("test", it.authenticationSource.provider)
                assertEquals("", it.authenticationSource.key)
                assertEquals("test-group", it.name)
                assertEquals(name, it.group.name)
            }
        }
    }

    @Test
    fun `Updating existing mappings`() {
        asAdmin {
            val name = uid("g")
            repeat(2) {
                casc("""
                    ontrack:
                        admin:
                            groups:
                                - name: $name
                                  description: My group
                            group-mappings:
                                - provider: test
                                  providerKey: ""
                                  providerGroup: "test-group"
                                  group: $name
                """.trimIndent())
            }
            val mapping = accountService.findAccountGroupByName(name)?.let {
                accountGroupMappingService.getMappingsForGroup(it)
            }?.firstOrNull()
            assertNotNull(mapping, "Mapping created") {
                assertEquals("test", it.authenticationSource.provider)
                assertEquals("", it.authenticationSource.key)
                assertEquals("test-group", it.name)
                assertEquals(name, it.group.name)
            }
        }
    }

    @Test
    fun `Mapping fails if group is not declared`() {
        asAdmin {
            val name = uid("g")
            assertFailsWith<IllegalStateException> {
                casc("""
                    ontrack:
                        admin:
                            group-mappings:
                                - provider: test
                                  providerKey: ""
                                  providerGroup: "test-group"
                                  group: $name
                """.trimIndent())
            }
        }
    }

    @Test
    fun `Creation of mappings before the groups fails`() {
        asAdmin {
            val name = uid("g")
            assertFailsWith<IllegalStateException> {
                casc("""
                ontrack:
                    admin:
                        group-mappings:
                            - provider: test
                              providerKey: ""
                              providerGroup: "test-group"
                              group: $name
                        groups:
                            - name: $name
                              description: My group
            """.trimIndent())
            }
        }
    }

}