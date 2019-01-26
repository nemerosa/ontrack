package net.nemerosa.ontrack.acceptance.browser.dialogs

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.support.AbstractDialog
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.Select

class PromotionRunDialog extends AbstractDialog<PromotionRunDialog> {

    @FindBy(id = "promotionLevelId")
    private WebElement promotionLevelSelect

    PromotionRunDialog(Browser browser) {
        super(browser)
    }

    void setPromotion(String promotion) {
        new Select(promotionLevelSelect).selectByVisibleText(promotion)
    }
}
