package net.nemerosa.ontrack.extension.vault

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.ConfidentialStore
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import java.nio.charset.Charset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestPropertySource(
        properties = [
            "ontrack.config.key-store=vault",
            "ontrack.config.search.index.immediate=true"
        ]
)
@DirtiesContext
class VaultConfidentialStoreIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var store: ConfidentialStore

    @Test
    fun `Check Vault store is loaded`() {
        assertIs<VaultConfidentialStore>(store) {}
    }

    @Test
    fun `Storing and retrieving a key`() {
        val id = uid("K")
        store.store(id, KEY_BYTES)
        // Retriving the key
        val bytes = store.load(id)
        assertNotNull(bytes) {
            assertEquals(KEY_BYTES.toTypedArray().toList(), it.toTypedArray().toList())
        }
    }

    companion object {
        private val KEY_BYTES = "my-super-secret-key".toByteArray(Charset.forName("UTF-8"))
    }

}