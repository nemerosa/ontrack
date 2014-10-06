package net.nemerosa.ontrack.acceptance.browser;

import lombok.experimental.Delegate;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.util.Map;
import java.util.function.Consumer;

public class Browser {

    @Delegate
    private final Configuration configuration;

    protected Browser(Configuration configuration) {
        this.configuration = configuration;
    }

    public static void browser(Consumer<Browser> closure) {
        Configuration.driver(config -> {
            Browser browser = new Browser(config);
            closure.accept(browser);
        });
    }

    public <P extends Page> P goTo(Class<P> pageClass, Map<String, Object> parameters) {
        P page;
        try {
            page = ConstructorUtils.invokeExactConstructor(pageClass, this);
        } catch (Exception e) {
            throw new CannotBuildPageException(pageClass, e);
        }
        String path = page.getPath(parameters);
        configuration.goTo(path);
        page.waitFor();
        return page;
    }

}
