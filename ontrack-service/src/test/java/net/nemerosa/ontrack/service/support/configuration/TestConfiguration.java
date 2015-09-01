package net.nemerosa.ontrack.service.support.configuration;

import lombok.Data;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;

import java.util.function.Function;

@Data
public class TestConfiguration implements UserPasswordConfiguration<TestConfiguration> {

    private final String name;
    private final String user;
    private final String password;

    @Override
    public TestConfiguration withPassword(String password) {
        return new TestConfiguration(
                name,
                user,
                password
        );
    }

    @Override
    public TestConfiguration clone(String targetConfigurationName, Function<String, String> replacementFunction) {
        return new TestConfiguration(
                targetConfigurationName,
                user,
                password
        );
    }

    @Override
    public ConfigurationDescriptor getDescriptor() {
        return new ConfigurationDescriptor("test", name);
    }

    @Override
    public TestConfiguration obfuscate() {
        return new TestConfiguration(
                name,
                user,
                ""
        );
    }

    public static final String PLAIN_PASSWORD = "verysecret";

    public static TestConfiguration config(String name) {
        return new TestConfiguration(name, "user", PLAIN_PASSWORD);
    }
}
