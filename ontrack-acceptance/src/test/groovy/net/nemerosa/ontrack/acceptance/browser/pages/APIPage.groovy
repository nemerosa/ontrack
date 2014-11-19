package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser

class APIPage extends AbstractHeaderPage {

    APIPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/api/${parameters.link}"
    }

    String getApiLink() {
        $('.ot-api-link').text.trim()
    }
}
