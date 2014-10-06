package net.nemerosa.ontrack.acceptance.browser.support;

import net.nemerosa.ontrack.acceptance.browser.Browser;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractModule {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Browser browser;
    protected final WebDriver driver;

    protected AbstractModule(Browser browser) {
        this.browser = browser;
        this.driver = browser.getDriver();
    }

    public void trace(String message, Object... parameters) {
        logger.debug("[gui] {}", String.format(message, parameters));
    }

}
