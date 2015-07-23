package net.nemerosa.ontrack.acceptance.boot;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest;
import net.nemerosa.ontrack.client.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

import static java.lang.String.format;

@Data
@Component
@ConfigurationProperties(prefix = "ontrack")
class AcceptanceConfig {

    private final Logger logger = LoggerFactory.getLogger(AcceptanceConfig.class);

    private String url; // Required
    private boolean disableSsl = false;
    private String admin = "admin";
    private Set<String> context = Collections.emptySet();
    private int timeout = 120;
    private int implicitWait = 5; // GUI implicit wait, in seconds

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
        String[] excludes = acceptanceTest.excludes();
        // No exclusion must be contained in the context
        return CollectionUtils.intersection(
                Arrays.asList(excludes),
                context
        ).isEmpty();
    }

    public void setSystemProperties() {
        System.setProperty("ontrack.url", url);
        System.setProperty("ontrack.admin", admin);
        System.setProperty("ontrack.disableSSL", String.valueOf(disableSsl));
        System.setProperty("ontrack.implicitWait", String.valueOf(implicitWait));
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
