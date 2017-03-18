package net.nemerosa.ontrack.acceptance.config;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class AcceptanceConfigRule implements TestRule {

    /**
     * Storing the configuration
     */
    private static final ThreadLocal<AcceptanceConfig> CONFIG_HANDLE = new ThreadLocal<>();

    /**
     * Sets the global configuration
     */
    public static void setGlobalConfig(AcceptanceConfig config) {
        CONFIG_HANDLE.set(config);
    }

    /**
     * Local configuration
     */
    private final AcceptanceConfig config;

    /**
     * Gets the current configuration
     */
    public AcceptanceConfigRule() {
        AcceptanceConfig globalConfig = CONFIG_HANDLE.get();
        if (globalConfig != null) {
            config = globalConfig;
        } else {
            config = AcceptanceConfig.fromEnv();
        }
    }

    /**
     * Access to the local configuration
     */
    public AcceptanceConfig getConfig() {
        return config;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return base;
    }
}
