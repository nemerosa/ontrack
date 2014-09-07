package net.nemerosa.ontrack.acceptance

import com.google.common.base.Predicate
import net.nemerosa.ontrack.acceptance.pages.HomePage
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.openqa.selenium.*
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

abstract class GUITestClient extends AcceptanceTestClient {

    private static final Logger logger = LoggerFactory.getLogger(GUITestClient)

    private static int implicitWait
    private static File screenshotDir

    protected static WebDriver driver
    private static AtomicLong screenshotIndex = new AtomicLong()

    @BeforeClass
    static void init() {
        // Configuration
        implicitWait = Integer.parseInt(
                env('ontrack.implicitWait', false, '5', "Implicit wait time for GUI components (in seconds)"),
                10)
        screenshotDir = new File(env('ontrack.acceptance.screenshots', false, 'build/acceptance/screenshots', 'Screenshot output directory')).absoluteFile
        FileUtils.forceMkdir(screenshotDir)
        // Logging
        logger.info("[gui] Implicit wait = ${implicitWait}s")
        logger.info("[gui] Screenshots   = ${screenshotDir}")
        // Web driver class
        driver = initDriver()
        driver.manage().deleteAllCookies()
        driver.manage().window().setSize(new Dimension(1024, 768))
        driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
    }

    @AfterClass
    static void quit() {
        if (driver != null) {
            driver.quit()
        }
    }

    @After
    void after() {
        screenshot('end')
        driver.manage().deleteAllCookies()
    }

    static WebDriver initDriver() {
        def loggingDir = new File(env('ontrack.acceptance.logs', false, 'build/acceptance/logs', 'Logging output directory')).absoluteFile
        FileUtils.forceMkdir(loggingDir)
        logger.info "[gui] Browser logging directory at ${loggingDir}"

        FirefoxProfile profile = new FirefoxProfile()
        profile.setPreference('webdriver.log.browser.file', new File(loggingDir, 'browser.log').absolutePath)
        profile.setPreference('webdriver.log.browser.level', 'all')

        new FirefoxDriver(profile)
    }

    static String initUrl() {
        System.getProperty('ontrack.url') ?: 'http://localhost:8080'
    }

    static HomePage startApplication() {
        def url = initUrl()
        driver.get("${url}/index.html")
        new HomePage(driver)
    }

    static String getAdminPassword() {
        System.getProperty('ontrack.admin.password') ?: 'admin'
    }

    static def waitUntil(Closure<Boolean> closure) {
        waitUntil(implicitWait, closure)
    }

    static def screenshot(String name) {
        // Screenshot name
        String fullName = String.format(
                "%s-%d-%s.png",
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
                screenshotIndex.incrementAndGet(),
                name
        )
        // Saves the screenshot in the target directory
        def targetFile = new File(screenshotDir, fullName)
        logger.info("[gui] Screenshot at ${targetFile.absolutePath}");
        // Takes the screenshot
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Copies the file
        FileUtils.copyFile(scrFile, targetFile)
    }

    static def waitUntil(int seconds, Closure<Boolean> closure) {
        try {
            new WebDriverWait(driver, seconds).until(new Predicate<WebDriver>() {
                @Override
                boolean apply(WebDriver input) {
                    closure()
                }
            })
        } catch (TimeoutException ex) {
            // Takes a screenshot
            screenshot("timeout");
            // The error is still there
            throw ex;
        }
    }

}
