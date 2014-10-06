package net.nemerosa.ontrack.acceptance.browser.pages;

import net.nemerosa.ontrack.acceptance.browser.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Map;
import java.util.function.Function;

public class HomePage extends AbstractHeaderPage {

    @FindBy(className = "ot-command-project-new")
    private WebElement createProjectCommand;

    public HomePage(Browser browser) {
        super(browser);
    }

    @Override
    public String getPath(Map<String, Object> parameters) {
        return "index.html";
    }

    public <T> T createProject(Function<ProjectDialog, T> closure) {
        browser.waitUntil(createProjectCommand::isDisplayed);
        createProjectCommand.click();
        ProjectDialog dialog = new ProjectDialog(browser).waitFor();
        try {
            return closure.apply(dialog);
        } finally {
            dialog.ok();
        }
    }

    public boolean isProjectPresent(String name) {
        return browser.waitUntil(() -> driver.findElement(By.linkText(name)).isDisplayed());
    }

}
