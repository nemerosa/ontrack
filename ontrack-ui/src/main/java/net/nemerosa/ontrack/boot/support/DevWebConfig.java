package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.common.RunProfile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Profile(RunProfile.DEV)
public class DevWebConfig extends WebMvcConfigurerAdapter {

    private final Log log = LogFactory.getLog(DevWebConfig.class);

    @Autowired
    private DevSettings devSettings;

    /**
     * At development time, we want the static resources served directly
     * from the <code>ontrack-web</code> project, under the <code>target/dev</code>
     * directory.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String staticDir = devSettings.getStaticDir();
        log.info("Static web resources from: " + staticDir);
        String prefix = "file:";
        String dir = prefix + staticDir;
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        registry.addResourceHandler("/app/**").addResourceLocations(dir + "app/");
        registry.addResourceHandler("/assets/**").addResourceLocations(dir + "assets/");
        registry.addResourceHandler("/fonts/**").addResourceLocations(dir + "fonts/");
        registry.addResourceHandler("/vendor/**").addResourceLocations(dir + "vendor/");
        registry.addResourceHandler("index.html").addResourceLocations(dir);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

}
