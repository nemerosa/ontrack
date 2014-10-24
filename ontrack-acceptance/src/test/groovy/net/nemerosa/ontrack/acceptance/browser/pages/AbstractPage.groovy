package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.Page
import net.nemerosa.ontrack.acceptance.browser.support.AbstractModule

public abstract class AbstractPage extends AbstractModule implements Page {
    public AbstractPage(Browser browser) {
        super(browser);
    }
}
