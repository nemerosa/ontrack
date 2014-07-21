package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.pages.HomePage
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver

abstract class GUITestClient extends AcceptanceTestClient {

    protected static WebDriver driver

    @BeforeClass
    static void init() {
        // Web driver class
        driver = initDriver()
        driver.manage().deleteAllCookies()
        driver.manage().window().setSize(new Dimension(1024, 768))
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

}
