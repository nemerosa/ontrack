package net.nemerosa.ontrack.acceptance

import com.google.common.base.Predicate
import net.nemerosa.ontrack.acceptance.pages.HomePage
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.WebDriverWait

import java.util.concurrent.TimeUnit

abstract class GUITestClient extends AcceptanceTestClient {

    protected static WebDriver driver

    @BeforeClass
    static void init() {
        // Web driver class
        driver = initDriver()
        driver.manage().deleteAllCookies()
        driver.manage().window().setSize(new Dimension(1024, 768))
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @AfterClass
    static void quit() {
        if (driver != null) {
            driver.quit()
        }
    }

    @After
    void after() {
        driver.manage().deleteAllCookies()
    }

    static WebDriver initDriver() {
        new FirefoxDriver()
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
        waitUntil(2, closure)
    }

    static def waitUntil(int seconds, Closure<Boolean> closure) {
        new WebDriverWait(driver, seconds).until(new Predicate<WebDriver>() {
            @Override
            boolean apply(WebDriver input) {
                closure()
            }
        })
    }

}
