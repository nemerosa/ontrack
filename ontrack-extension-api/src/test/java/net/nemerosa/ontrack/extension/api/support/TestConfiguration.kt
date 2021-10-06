package net.nemerosa.ontrack.extension.api.support;

import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;

public class TestConfiguration extends UserPasswordConfiguration<TestConfiguration> {

    public TestConfiguration(String name, String user, String password) {
        super(name, user, password);
    }

    @Override
    public TestConfiguration withPassword(String password) {
        return new TestConfiguration(
                getName(),
                getUser(),
                password
        );
    }

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor("test", getName());
    }

    @Override
    public TestConfiguration obfuscate() {
        return new TestConfiguration(
                getName(),
                getUser(),
                ""
        );
    }

    public static final String PLAIN_PASSWORD = "verysecret";

    public static TestConfiguration config(String name) {
        return new TestConfiguration(name, "user", PLAIN_PASSWORD);
    }
}
