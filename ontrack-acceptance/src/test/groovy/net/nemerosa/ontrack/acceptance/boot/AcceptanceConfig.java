package net.nemerosa.ontrack.acceptance.boot;

import lombok.Data;
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "ontrack")
class AcceptanceConfig {

    private String url; // Required
    private boolean disableSsl = false;
    private String admin = "admin";
    private Set<String> context = Collections.emptySet();

    @PostConstruct
    void check() {
        if (StringUtils.isBlank(url)) {
            throw new AcceptanceMissingURLException();
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
    }
}
