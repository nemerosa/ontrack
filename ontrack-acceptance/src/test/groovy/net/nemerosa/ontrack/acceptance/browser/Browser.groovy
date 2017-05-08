package net.nemerosa.ontrack.acceptance.browser

import org.apache.commons.lang3.reflect.ConstructorUtils

class Browser {

    @Delegate
    private final Configuration configuration;

    Browser(Configuration configuration) {
        this.configuration = configuration;
    }

    Configuration getConfiguration() {
        configuration
    }

    def <P extends Page> P goTo(Class<P> pageClass, Map<String, Object> parameters = [:], boolean wait = true) {
        P page = page(pageClass)
        String path = page.getPath(parameters);
        configuration.goTo(path);
        if (wait) {
            page.waitFor();
        }
        return page;
    }

    def <P extends Page> P at(Class<P> pageClass) {
        P page = page(pageClass)
        page.waitFor()
        page
    }

    def <P extends Page> P page(Class<P> pageClass) {
        P page;
        try {
            page = ConstructorUtils.invokeExactConstructor(pageClass, this);
        } catch (Exception e) {
            throw new CannotBuildPageException(pageClass, e);
        }
        page
    }
}
