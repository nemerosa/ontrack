package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private URIBuilder uriBuilder;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private List<ResourceModule> resourceModules;

    /**
     * Uses the HTTP header for content negociation.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(false);
        configurer.favorPathExtension(false);
    }

    /**
     * ETag support
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.clear();
        // Plain text
        converters.add(new StringHttpMessageConverter());
        // Documents
        converters.add(new DocumentHttpMessageConverter());
        // JSON
        converters.add(new ResourceHttpMessageConverter(uriBuilder, securityService, resourceModules));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/graphql/**");
        registry.addMapping("/rest/**");
        registry.addMapping("/accounts/**");
        registry.addMapping("/admin/**");
        registry.addMapping("/api/**");
        registry.addMapping("/structure/**");
        registry.addMapping("/branches/**");
        registry.addMapping("/events/**");
        registry.addMapping("/info/**");
        registry.addMapping("/properties/**");
        registry.addMapping("/search/**");
        registry.addMapping("/settings/**");
        registry.addMapping("/user/**");
        registry.addMapping("/validation-stamp-filters/**");
    }

}
