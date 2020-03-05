package net.nemerosa.ontrack.acceptance;

public interface AcceptanceTestContext {

    String PRODUCTION = "production";
    String SMOKE = "smoke";
    String BROWSER_TEST = "browser-test";
    /**
     * For testing extensions
     */
    String EXTENSIONS = "extensions";

    /**
     * For testing Vault integration
     */
    String VAULT = "vault";

}
