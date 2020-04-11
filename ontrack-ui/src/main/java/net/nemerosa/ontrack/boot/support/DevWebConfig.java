package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.common.RunProfile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@Profile(RunProfile.DEV)
public class DevWebConfig implements WebMvcConfigurer {

    private final Log log = LogFactory.getLog(DevWebConfig.class);

    @Autowired
    private DevSettings devSettings;

    /**
     * At development time, we want the static resources served directly
     * from the <code>ontrack-web</code> project, under the <code>build/web/dev</code> and <code>build/web/prod</code>
     * directories.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Warning
        log.warn("[dev] Running in DEV mode");

        // Compiled production resources

        String prod = getPath(devSettings.getProd());
        log.info("[dev] Compiled prod web resources from: " + prod);
        registry.addResourceHandler("/fonts/**").addResourceLocations(prod + "/fonts/");

        // Compiler dev resources

        String dev = getPath(devSettings.getDev());
        log.info("[dev] Compiled dev web resources from: " + dev);
        registry.addResourceHandler("/css/**").addResourceLocations(dev + "/css/");
        registry.addResourceHandler("/templates/**").addResourceLocations(dev + "/templates/");
        registry.addResourceHandler("/converted/**").addResourceLocations(dev + "/converted/");
        registry.addResourceHandler("index.html").addResourceLocations(dev + "/");
        registry.addResourceHandler("graphiql.html").addResourceLocations(dev + "/");

        // Direct access to the sources

        String source = getPath(devSettings.getSrc());
        log.info("[dev] Web sources from: " + source);
        registry.addResourceHandler("/app/**").addResourceLocations(source + "/app/");
        registry.addResourceHandler("/graphiql/**").addResourceLocations(source + "/graphiql/");
        registry.addResourceHandler("/assets/**").addResourceLocations(source + "/assets/");

        // Vendor resources
        String vendor = getPath(devSettings.getVendor());
        log.info("[dev] Vendor sources from: " + vendor);
        registry.addResourceHandler("/vendor/**").addResourceLocations(vendor + "/");
    }

    private String getPath(String dirName) {
        return "file:" + new File(
                devSettings.getWeb(),
                dirName
        ).getAbsolutePath();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

}
