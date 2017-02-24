package net.nemerosa.ontrack.acceptance.config;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.nemerosa.ontrack.acceptance.boot.AcceptanceMissingURLException;
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest;
import net.nemerosa.ontrack.client.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static java.lang.String.format;

@Data
@Component
@ConfigurationProperties(prefix = "ontrack.acceptance")
public class AcceptanceConfig {

    private final Logger logger = LoggerFactory.getLogger(AcceptanceConfig.class);

    public static final String CONTEXT_ALL = "all";

    /**
     * URL of the Ontrack instance to test
     */
    private String url = "http://localhost:8080";
    /**
     * Optional Selenium Grid/Node URL
     */
    private String seleniumUrl = "";
    /**
     * Disabling the SSL checks
     */
    private boolean disableSsl = false;
    /**
     * Admin password
     */
    private String admin = "admin";
    /**
     * Tag for identifying the tests to run
     */
    private String context = null;
    /**
     * Timeout in s to wait for Ontrack to start
     */
    private int timeout = 120;
    /**
     * GUI elements waiting time (in s)
     */
    private int implicitWait = 5;
    /**
     * Output directory
     */
    private File outputDir = new File("build/acceptance");
    /**
     * Name for the results file.
     */
    private String resultFileName = "ontrack-acceptance.xml";

    @PostConstruct
    void check() throws InterruptedException, ExecutionException, TimeoutException {
        // Checks the URL is defined
        if (StringUtils.isBlank(url)) {
            throw new AcceptanceMissingURLException();
        }
        // Checks until the /info is OK
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            logger.info(format("Waiting %d s for %s to be available...", timeout, url));
            AcceptanceTargetCheck check = new AcceptanceTargetCheck();
            Future<String> future = executor.submit(check);
            String version = future.get(timeout, TimeUnit.SECONDS);
            logger.info(format("Getting version %s", version));
        } finally {
            executor.shutdownNow();
        }
    }

    public boolean acceptTest(AcceptanceTest acceptanceTest) {
        // No context, all tests are eligible
        // If there is a context, but no annotation, the test cannot be accepted
        // There is a context *and* an annotation, checks the context is part of the
        // accepted values in the annotation
        return StringUtils.isBlank(context) || StringUtils.equals(CONTEXT_ALL, context) ||
                (
                        acceptanceTest != null &&
                                Arrays.asList(acceptanceTest.value()).contains(context)
                );
    }

    public void log(Consumer<String> logger) {
        logger.accept(String.format(">>> Ontrack URL:      %s", url));
        logger.accept(String.format(">>> Selenium URL:     %s", seleniumUrl));
        logger.accept(String.format(">>> Disable SSL:      %s", disableSsl));
        logger.accept(String.format(">>> Admin password:   %s", "admin".equals(admin) ? "admin" : "****"));
        logger.accept(String.format(">>> Context:          %s", context));
        logger.accept(String.format(">>> Timeout:          %s", timeout));
        logger.accept(String.format(">>> Implicit wait:    %s", implicitWait));
        logger.accept(String.format(">>> Output directory: %s", outputDir));
        logger.accept(String.format(">>> Result file name: %s", resultFileName));
    }

    public static AcceptanceConfig fromEnv() {
        AcceptanceConfig c = new AcceptanceConfig();
        c.setUrl(env("ontrack.acceptance.url", c.getUrl(), "Ontrack URL"));
        c.setSeleniumUrl(env("ontrack.acceptance.selenium-url", c.getSeleniumUrl(), "Selenium URL"));
        c.setDisableSsl(envAsBoolean("ontrack.acceptance.disable-ssl", c.isDisableSsl(), "Disabling SSL"));
        c.setAdmin(env("ontrack.acceptance.admin", c.getAdmin(), "Admin password"));
        c.setContext(env("ontrack.acceptance.context", c.getContext(), "Test context"));
        c.setTimeout(envAsInt("ontrack.acceptance.timeout", c.getTimeout(), "Timeout for Ontrack (s)"));
        c.setImplicitWait(envAsInt("ontrack.acceptance.implicit-wait", c.getImplicitWait(), "GUI element wait (s)"));
        c.setOutputDir(envAsFile("ontrack.acceptance.output-dir", c.getOutputDir(), "Output directory"));
        c.setResultFileName(env("ontrack.acceptance.result-file-name", "ontrack-acceptance.xml", "Output directory"));
        return c;
    }

    private static String env(String property, String defaultValue, String name) {
        String sys = System.getProperty(property);
        if (StringUtils.isNotBlank(sys)) {
            return sys;
        } else {
            String envName = property.toUpperCase().replace(".", "_");
            String env = System.getenv(envName);
            if (StringUtils.isNotBlank(env)) {
                return env;
            } else {
                return defaultValue;
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean envAsBoolean(String property, boolean defaultValue, String name) {
        String value = env(property, null, name);
        if (value == null) {
            return defaultValue;
        } else {
            return Boolean.valueOf(value);
        }
    }

    private static int envAsInt(String property, int defaultValue, String name) {
        String value = env(property, null, name);
        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value, 10);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static File envAsFile(String property, File defaultValue, String name) {
        String value = env(property, null, name);
        if (value == null) {
            return defaultValue;
        } else {
            return new File(value);
        }
    }

    private class AcceptanceTargetCheck implements Callable<String> {

        @Override
        public String call() throws Exception {
            // Client
            OTHttpClient client = OTHttpClientBuilder.create(url, disableSsl).build();
            JsonClient jsonClient = new JsonClientImpl(client);
            // Gets the info every second
            while (true) {
                try {
                    // Gets the info
                    JsonNode info = jsonClient.get("info");
                    // Gets the version
                    return info.path("version").path("display").asText();
                } catch (ClientException ex) {
                    logger.debug(ex.getMessage());
                }
                // Waits
                Thread.sleep(1_000);
            }
        }
    }
}
