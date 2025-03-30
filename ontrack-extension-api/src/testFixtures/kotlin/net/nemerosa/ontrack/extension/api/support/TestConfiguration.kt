package net.nemerosa.ontrack.extension.api.support

import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

class TestConfiguration(
    name: String, user: String, password: String?
) : UserPasswordConfiguration<TestConfiguration>(
    name, user, password
) {

    override fun withPassword(password: String?): TestConfiguration {
        return TestConfiguration(
            name,
            user ?: "",
            password
        )
    }

    override val descriptor: ConfigurationDescriptor
        get() = ConfigurationDescriptor("test", name)

    override fun obfuscate(): TestConfiguration {
        return TestConfiguration(
            name,
            user ?: "",
            ""
        )
    }

    companion object {
        const val PLAIN_PASSWORD = "verysecret"
        @JvmStatic
        fun config(name: String) = TestConfiguration(name, "user", PLAIN_PASSWORD)
    }
}