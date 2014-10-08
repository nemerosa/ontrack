package net.nemerosa.ontrack.acceptance.browser

import com.google.common.base.Function
import com.google.common.base.Predicate
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer

public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private final WebDriver driver;

    private final String baseUrl;
    private final int implicitWait;
    private final File screenshotDir;

    private final AtomicLong screenshotIndex = new AtomicLong();

    protected Configuration() {
        try {
            // Configuration
            baseUrl = env("ontrack.url", false, "http://localhost:8080", "Base URL");
            implicitWait = Integer.parseInt(
                    env("ontrack.implicitWait", false, "5", "Implicit wait time for GUI components (in seconds)"),
                    10);
            screenshotDir = new File(env("ontrack.acceptance.screenshots", false, "build/acceptance/screenshots", "Screenshot output directory")).getAbsoluteFile();
            FileUtils.forceMkdir(screenshotDir);
            // Logging
            logger.info("[gui] Base URl      = {}", baseUrl);
            logger.info("[gui] Implicit wait = {}s", implicitWait);
            logger.info("[gui] Screenshots   = {}", screenshotDir);
            // Web driver class
            driver = initDriver();
            driver.manage().deleteAllCookies();
            driver.manage().window().setSize(new Dimension(1024, 768));
            driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        } catch (IOException ex) {
            throw new ConfigurationException("Cannot initialise browser configuration", ex);
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void closeConfiguration() {
        driver.quit();
    }

    public void goTo(String path) {
        logger.info("Go to: ${}", path);
        driver.get(String.format("%s/%s", baseUrl, path));
    }

    public WebElement findElement(By by) {
        new FluentWait<WebDriver>(driver)
                .withTimeout(implicitWait, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .until(
                new Function<WebDriver, WebElement>() {
                    public WebElement apply(WebDriver driver) {
                        return driver.findElement(by);
                    }
                }
        )
    }

    public void waitUntil(Closure<Boolean> closure) {
        waitUntil("element", closure);
    }

    public void waitUntil(String message, Closure<Boolean> closure) {
        waitUntil(message, implicitWait, closure);
    }

    public void waitUntil(String message, int seconds, Closure<Boolean> closure) {
        try {
            new WebDriverWait(driver, seconds).until(new Predicate<WebDriver>() {
                @Override
                boolean apply(WebDriver input) {
                    closure()
                }
            });
        } catch (TimeoutException ex) {
            // Takes a screenshot
            screenshot("timeout");
            // The error is still there
            throw new TimeoutException("Could not get ${message} in time", ex);
        }
    }

    public void screenshot(String name) {
        // Screenshot name
        String fullName = String.format(
                "%s-%d-%s.png",
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
                screenshotIndex.incrementAndGet(),
                name
        );
        // Saves the screenshot in the target directory
        File targetFile = new File(screenshotDir, fullName);
        logger.info("[gui] Screenshot at {}", targetFile.getAbsolutePath());
        // Takes the screenshot
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Copies the file
        try {
            FileUtils.copyFile(scrFile, targetFile);
        } catch (IOException e) {
            throw new CannotTakeScreenshotException(name, e);
        }
    }

    public static void driver(Consumer<Configuration> closure) {
        // Loads the driver environment
        Configuration configuration = new Configuration();
        try {
            // Runs with the driver
            closure.accept(configuration);
        } finally {
            // Closes the driver
            configuration.closeConfiguration();
        }
    }

    static String getAdminPassword() {
        env('ontrack.admin.password', false, 'admin', "Admin password")
    }

    public static String env(String property, boolean required, String defaultValue, String name) {
        String sys = System.getProperty(property);
        if (StringUtils.isNotBlank(sys)) {
            return sys;
        } else {
            String envName = property.toUpperCase().replace(".", "_");
            String env = System.getenv(envName);
            if (StringUtils.isNotBlank(env)) {
                return env;
            } else if (required) {
                throw new IllegalStateException(
                        String.format(
                                "The %s system property or %s environment variable is required (%s)",
                                property,
                                envName,
                                name
                        )
                );
            } else {
                return defaultValue;
            }
        }
    }

    public static WebDriver initDriver() throws IOException {
        File loggingDir = new File(env("ontrack.acceptance.logs", false, "build/acceptance/logs", "Logging output directory")).getAbsoluteFile();
        FileUtils.forceMkdir(loggingDir);
        logger.info("[gui] Browser logging directory at {}", loggingDir);

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("webdriver.log.browser.file", new File(loggingDir, "browser.log").getAbsolutePath());
        profile.setPreference("webdriver.log.browser.level", "all");

        return new FirefoxDriver(profile);
    }

}
