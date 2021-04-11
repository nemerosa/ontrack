package net.nemerosa.ontrack.acceptance.browser.support

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public abstract class AbstractModule {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Browser browser;
    protected final WebDriver driver;

    protected AbstractModule(Browser browser) {
        this.browser = browser
        this.driver = browser.driver
        PageFactory.initElements(driver, this)
    }

    public void trace(String message, Object... parameters) {
        logger.debug("[gui] {}", String.format(message, parameters));
    }

    public WebElement $(By by) {
        browser.findElement(by)
    }

    public WebElement $(String css) {
        browser.findElement(By.cssSelector(css))
    }

    boolean isElementDisplayed(By by) {
        def elements = browser.findElements(by)
        if (elements.isEmpty()) {
            return false
        } else {
            def element = elements.first()
            return element.isDisplayed()
        }
    }

}
