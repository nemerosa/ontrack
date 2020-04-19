package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.Page
import net.nemerosa.ontrack.acceptance.browser.modules.HeaderModule
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

public abstract class AbstractHeaderPage extends AbstractPage {

    @FindBy(className = 'ot-view-title')
    protected WebElement pageTitle;

    @FindBy(id = 'header-user-menu')
    protected WebElement userMenu

    private final HeaderModule header;

    public AbstractHeaderPage(Browser browser) {
        super(browser);
        header = new HeaderModule(browser);
    }

    HeaderModule getHeader() {
        return header
    }

    @Override
    public void waitFor() {
        browser.waitUntil("Page title") { pageTitle.displayed }
    }

    /**
     * Navigates to a user menu item using its ID and returns the target page
     */
    def <P extends Page> P selectUserMenu(Class<P> pageClass, String id) {
        // Clicks on the user menu
        userMenu.click()
        // Item to select
        WebElement item = browser.findElement(By.id(id))
        // Waits until the item is selected
        browser.waitUntil(id) { item.displayed }
        // Clicks on the item
        item.click()
        // Selects the page
        P page = browser.page(pageClass)
        // Waits for the page
        page.waitFor()
        // OK
        return page
    }

    APIPage goToAPI() {
        $('.ot-command-api').click()
        browser.at(APIPage)
    }

    Collection<WebElement> findDecorations(String decorationType) {
        return browser.findElements(By.className(
                "ot-decoration-${decorationType}"
        ))
    }
}
