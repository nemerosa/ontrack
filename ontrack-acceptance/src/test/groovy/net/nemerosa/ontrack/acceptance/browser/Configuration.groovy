package net.nemerosa.ontrack.acceptance.browser

import com.google.common.base.Function
import com.google.common.base.Predicate
import net.nemerosa.ontrack.acceptance.config.AcceptanceConfig
import org.apache.commons.io.FileUtils
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class)

    private final WebDriver driver

    private final AcceptanceConfig acceptanceConfig
    private final String baseUrl
    private final int implicitWait
    private final File screenshotDir

    private final AtomicLong screenshotIndex = new AtomicLong()

    protected Configuration(AcceptanceConfig config) {
        try {
            acceptanceConfig = config
            // Configuration
            baseUrl = config.url
            implicitWait = config.implicitWait
            screenshotDir = new File(config.outputDir, "screenshots").getAbsoluteFile()
            FileUtils.forceMkdir(screenshotDir)
            // Web driver class
            driver = initDriver(config)
            driver.manage().deleteAllCookies()
            driver.manage().window().setSize(new Dimension(1024, 768))
            driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS)
        } catch (IOException ex) {
            throw new ConfigurationException("Cannot initialise browser configuration", ex)
        }
    }

    AcceptanceConfig getAcceptanceConfig() {
        return acceptanceConfig
    }

    WebDriver getDriver() {
        return driver
    }

    String getBaseUrl() {
        return baseUrl
    }

    void closeConfiguration() {
        driver.quit()
    }

    void goTo(String path) {
        logger.info("Go to: ${}", path)
        driver.get(String.format("%s/%s", baseUrl, path))
    }

    WebElement findElement(By by) {
        new FluentWait<WebDriver>(driver)
                .withTimeout(implicitWait, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(
                new Function<WebDriver, WebElement>() {
                    WebElement apply(WebDriver driver) {
                        return driver.findElement(by)
                    }
                }
        )
    }

    Collection<WebElement> findElements(By by) {
        new FluentWait<WebDriver>(driver)
                .withTimeout(implicitWait, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(
                new Function<WebDriver, Collection<WebElement>>() {
                    Collection<WebElement> apply(WebDriver driver) {
                        return driver.findElements(by)
                    }
                }
        )
    }

    void waitUntil(Closure<Boolean> closure) {
        waitUntil("element", closure)
    }

    void waitUntil(String message, Closure<Boolean> closure) {
        waitUntil(message, implicitWait, closure)
    }

    void waitUntil(String message, int seconds, Closure<Boolean> closure) {
        try {
            new WebDriverWait(driver, seconds).until(new Predicate<WebDriver>() {
                @Override
                boolean apply(WebDriver input) {
                    closure()
                }
            })
        } catch (TimeoutException ex) {
            // Takes a screenshot
            screenshot("timeout")
            // The error is still there
            throw new TimeoutException("Could not get ${message} in less than ${seconds} seconds", ex)
        }
    }

    void screenshot(String name) {
        // Screenshot name
        String fullName = String.format(
                "%s-%d-%s.png",
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
                screenshotIndex.incrementAndGet(),
                name
        )
        // Saves the screenshot in the target directory
        File targetFile = new File(screenshotDir, fullName)
        logger.info("[gui] Screenshot at {}", targetFile.getAbsolutePath())
        // Takes the screenshot
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)
        // Copies the file
        try {
            FileUtils.copyFile(scrFile, targetFile)
        } catch (IOException e) {
            throw new CannotTakeScreenshotException(name, e)
        }
    }

    static void driver(AcceptanceConfig config, Consumer<Configuration> closure) {
        // Loads the driver environment
        Configuration configuration = new Configuration(config)
        try {
            // Runs with the driver
            closure.accept(configuration)
        } finally {
            // Closes the driver
            configuration.closeConfiguration()
        }
    }

    static WebDriver initDriver(AcceptanceConfig config) throws IOException {
        File loggingDir = new File(config.outputDir, "logs").getAbsoluteFile()
        FileUtils.forceMkdir(loggingDir)
        logger.info("[gui] Browser logging directory at {}", loggingDir)

        if (config.seleniumUrl) {
            DesiredCapabilities desiredCapabilities = new DesiredCapabilities()
            desiredCapabilities.setBrowserName("firefox")
            return new RemoteWebDriver(
                    new URL(config.seleniumUrl),
                    desiredCapabilities
            )
        } else {
            FirefoxProfile profile = new FirefoxProfile()
            profile.setPreference("webdriver.log.browser.file", new File(loggingDir, "browser.log").getAbsolutePath())
            profile.setPreference("webdriver.log.browser.level", "all")
            return new FirefoxDriver(profile)
        }
    }

}
