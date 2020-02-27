package net.nemerosa.ontrack.extension.vault

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Test
import org.springframework.vault.core.VaultKeyValueOperations
import org.springframework.vault.core.VaultKeyValueOperationsSupport
import org.springframework.vault.core.VaultOperations
import org.springframework.vault.support.VaultResponseSupport
import java.nio.charset.Charset

class VaultConfidentialStoreTest {

    @Test
    fun default_config() {
        val vaultKvOps = mock<VaultKeyValueOperations>()
        val vaultOperations = mock<VaultOperations>()
        val configProperties = VaultConfigProperties()
        whenever(vaultOperations.opsForKeyValue("secret/ontrack/key", VaultKeyValueOperationsSupport.KeyValueBackend.unversioned())).thenReturn(vaultKvOps)
        val store = VaultConfidentialStore(
                vaultOperations,
                configProperties
        )

        store.store(KEY_NAME, KEY_BYTES)
        verify(vaultKvOps, times(1)).put(
                "my-key",
                Key(KEY_BYTES)
        )
        val response = VaultResponseSupport<Key>()
        response.data = Key(KEY_BYTES)
        whenever(vaultKvOps.get("my-key", Key::class.java)).thenReturn(
                response
        )
        val bytes = store.load(KEY_NAME)
        Assert.assertEquals(KEY_BYTES, bytes)
    }

    @Test
    fun custom_prefix() {
        val vaultKvOps = mock<VaultKeyValueOperations>()
        val vaultOperations = mock<VaultOperations>()
        whenever(vaultOperations.opsForKeyValue("custom/keys", VaultKeyValueOperationsSupport.KeyValueBackend.unversioned())).thenReturn(vaultKvOps)
        val configProperties = VaultConfigProperties()
        configProperties.prefix = "custom/keys"
        val store = VaultConfidentialStore(
                vaultOperations,
                configProperties
        )
        store.store(KEY_NAME, KEY_BYTES)
        verify(vaultKvOps, times(1)).put(
                "my-key",
                Key(KEY_BYTES)
        )
        val response = VaultResponseSupport<Key>()
        response.data = Key(KEY_BYTES)
        whenever(vaultKvOps.get("my-key", Key::class.java)).thenReturn(
                response
        )
        val bytes = store.load(KEY_NAME)
        Assert.assertEquals(KEY_BYTES, bytes)
    }

    companion object {
        private const val KEY_NAME = "my-key"
        private val KEY_BYTES = "my-super-secret-key".toByteArray(Charset.forName("UTF-8"))
    }
}