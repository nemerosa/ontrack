package net.nemerosa.ontrack.acceptance.browser

import net.nemerosa.ontrack.acceptance.browser.pages.ProjectPage
import org.apache.commons.lang3.reflect.ConstructorUtils

import java.lang.reflect.Type

public class Browser {

    @Delegate
    private final Configuration configuration;

    protected Browser(Configuration configuration) {
        this.configuration = configuration;
    }

    Configuration getConfiguration() {
        configuration
    }

    public <P extends Page> P goTo(Class<P> pageClass, Map<String, Object> parameters = [:], boolean wait = true) {
        P page = page(pageClass)
        String path = page.getPath(parameters);
        configuration.goTo(path);
        if (wait) {
            page.waitFor();
        }
        return page;
    }

    public static void browser(Closure closure) {
        Configuration.driver({ config ->
            Browser browser = new Browser(config)
            closure.delegate = browser
            closure(browser)
        })
    }

    def <P extends Page> P at(Class<P> pageClass) {
        P page = page(pageClass)
        page.waitFor()
        page
    }

    public <P extends Page> P page(Class<P> pageClass) {
        P page;
        try {
            page = ConstructorUtils.invokeExactConstructor(pageClass, this);
        } catch (Exception e) {
            throw new CannotBuildPageException(pageClass, e);
        }
        page
    }
}
