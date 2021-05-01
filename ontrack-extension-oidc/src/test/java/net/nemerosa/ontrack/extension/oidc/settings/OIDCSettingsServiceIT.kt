package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.extension.oidc.OidcAuthenticationSourceProvider
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProviderFixtures.testProvider
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.model.security.AccountInput
import net.nemerosa.ontrack.model.security.ProvidedGroupsService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.After
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class OIDCSettingsServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var service: OIDCSettingsService

    @Autowired
    private lateinit var accountGroupMappingService: AccountGroupMappingService

    @Autowired
    private lateinit var providedGroupsService: ProvidedGroupsService

    @After
    fun cleanup() {
        asAdmin {
            service.providers.forEach {
                service.deleteProvider(it.id)
            }
        }
    }

    @Test
    fun `Creating a provider`() {
        val id = uid("I")
        asAdmin {
            service.createProvider(testProvider(id))
            assertTrue(service.providers.any { it.id == id }, "Provider created")
        }
    }

    @Test
    fun `Client secret must be decrypted`() {
        val id = uid("I")
        asAdmin {
            service.createProvider(OntrackOIDCProvider(id, "Test", "", "", "", "xxxx", null))
            val provider = service.providers.find { it.id == id }
            assertNotNull(provider) {
                assertEquals("xxxx", it.clientSecret)
            }
        }
    }

    @Test
    fun `Deletion of a provider`() {
        val id = uid("I")
        asAdmin {
            service.createProvider(testProvider(id))
            assertTrue(service.providers.any { it.id == id }, "Provider created")
            service.deleteProvider(id)
            assertTrue(service.providers.none { it.id == id }, "Provider deleted")
        }
    }

    @Test
    fun `Deletion of an unknown provider`() {
        val id = uid("I")
        asAdmin {
            val ack = service.deleteProvider(id)
            assertFalse(ack.isSuccess)
        }
    }

    @Test
    fun `Updating an unknown provider`() {
        val id = uid("I")
        asAdmin {
            assertFailsWith<OntrackOIDCProviderIDNotFoundException> {
                service.updateProvider(testProvider(id))
            }
        }
    }

    @Test
    fun `Updating a provider`() {
        val id = uid("I")
        asAdmin {
            service.createProvider(testProvider(id))
            service.updateProvider(OntrackOIDCProvider(id, "Test 2", "", "", "xxxx", "yyyy", null))
            val provider = service.getProviderById(id) ?: error("Cannot find provider")
            assertEquals("Test 2", provider.name)
            assertEquals("xxxx", provider.clientId)
            assertEquals("yyyy", provider.clientSecret)
        }
    }

    @Test
    fun `Updating a provider does not update its secret if left blank in input`() {
        val id = uid("I")
        asAdmin {
            service.createProvider(OntrackOIDCProvider(id, "Test", "", "", "xxxx", "yyyy", null))
            service.updateProvider(OntrackOIDCProvider(id, "Test 2", "", "", "xxxx", "", null))
            val provider = service.getProviderById(id) ?: error("Cannot find provider")
            assertEquals("Test 2", provider.name)
            assertEquals("xxxx", provider.clientId)
            assertEquals("yyyy", provider.clientSecret)
        }
    }

    @Test
    fun `Creating a duplicate provider`() {
        val id = uid("I")
        asAdmin {
            service.createProvider(testProvider(id))
            assertFailsWith<OntrackOIDCProviderIDAlreadyExistsException> {
                service.createProvider(OntrackOIDCProvider(id, "Test 2", "", "", "", "", null))
            }
        }
    }

    @Test
    fun `Cached providers`() {
        val id = uid("I")
        asAdmin {
            service.createProvider(testProvider(id))
            val cache = service.cachedProviders
            val check = service.cachedProviders
            assertSame(cache, check, "Cached list")
        }
    }

    @Test
    fun `Cache cleaned after adding a provider`() {
        asAdmin {
            val cache = service.cachedProviders
            val check = service.cachedProviders
            assertSame(cache, check, "Cached list")
            val id = uid("I")
            service.createProvider(testProvider(id))
            val checkAgain = service.cachedProviders
            assertNotSame(check, checkAgain, "New cached list")
        }
    }

    @Test
    fun `Cache cleaned after deleting a provider`() {
        asAdmin {
            val id = uid("I")
            service.createProvider(testProvider(id))
            val cache = service.cachedProviders
            val check = service.cachedProviders
            assertSame(cache, check, "Cached list")
            service.deleteProvider(id)
            val checkAgain = service.cachedProviders
            assertNotSame(check, checkAgain, "New cached list")
        }
    }

    @Test
    fun `Cleanup of accounts after a provider is deleted`() {
        asAdmin {
            // Provider
            val id = uid("I")
            val provider = service.createProvider(testProvider(id))
            // Associated account
            val name = uid("U")
            val account = accountService.create(
                    AccountInput(
                            name = name,
                            fullName = "Test $name",
                            email = "$name@nemerosa.net",
                            password = null,
                            groups = null,
                            disabled = false,
                            locked = false,
                    ),
                    OidcAuthenticationSourceProvider.asSource(provider)
            )
            // Removes the provider
            service.deleteProvider(provider.id)
            // Checks the account is gone
            assertNull(accountService.findAccountByName(name), "Account is gone")
            // ... really gone
            assertFalse(accountService.doesAccountIdExist(account.id), "Account is gone")
        }
    }

    @Test
    fun `Cleanup of provided groups after a provider is deleted`() {
        asAdmin {
            // Provider
            val id = uid("I")
            val provider = service.createProvider(testProvider(id))
            val source = OidcAuthenticationSourceProvider.asSource(provider)
            // Associated account
            val name = uid("U")
            val account = accountService.create(
                    AccountInput(
                            name = name,
                            fullName = "Test $name",
                            email = "$name@nemerosa.net",
                            password = null,
                            groups = null,
                            disabled = false,
                            locked = false,
                    ),
                    source
            )
            // Associated provider group
            providedGroupsService.saveProvidedGroups(
                    account.id(),
                    source,
                    setOf("my-group")
            )
            // Checks the group is saved
            assertTrue(providedGroupsService.getProvidedGroups(account.id(), source).isNotEmpty(), "Provided groups are saved")
            // Removes the provider
            service.deleteProvider(provider.id)
            // Checks the group is gone
            assertTrue(providedGroupsService.getProvidedGroups(account.id(), source).isEmpty(), "Provided groups are gone")
        }
    }

    @Test
    fun `Cleanup of group mappings after a provider is deleted`() {
        asAdmin {
            // Provider
            val id = uid("I")
            val provider = service.createProvider(testProvider(id))
            val source = OidcAuthenticationSourceProvider.asSource(provider)
            // Creating a group
            val group = doCreateAccountGroup()
            // Creating a group mapping
            accountGroupMappingService.newMapping(
                    source,
                    AccountGroupMappingInput(
                            "my-mapping",
                            group.id
                    )
            )
            // Checks the group mapping is there
            assertTrue(accountGroupMappingService.getMappings(source).isNotEmpty(), "Group mappings are present")
            // Removes the provider
            service.deleteProvider(provider.id)
            // Checks the account group mapping is gone
            assertTrue(accountGroupMappingService.getMappings(source).isEmpty(), "Group mappings are gone")
        }
    }

}