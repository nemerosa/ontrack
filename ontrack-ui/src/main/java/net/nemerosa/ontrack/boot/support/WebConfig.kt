package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.resource.ResourceModule
import org.slf4j.LoggerFactory
import org.springframework.boot.task.TaskExecutorBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.filter.ShallowEtagHeaderFilter
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.annotation.PostConstruct
import javax.servlet.DispatcherType

@Configuration
class WebConfig(
        private val uriBuilder: URIBuilder,
        private val securityService: SecurityService,
        private val resourceModules: List<ResourceModule>,
        private val taskExecutorBuilder: TaskExecutorBuilder
) : WebMvcConfigurer {

    private val logger = LoggerFactory.getLogger(WebConfig::class.java)

    /**
     * Logging
     */
    @PostConstruct
    fun log() {
        logger.info("[web] URI builder = " + uriBuilder.javaClass.name)
    }

    override fun configureAsyncSupport(configurer: AsyncSupportConfigurer) {
        val executor = taskExecutorBuilder.build()
        executor.initialize()
        configurer.setTaskExecutor(executor)
        configurer.setDefaultTimeout(300000) // 5 minutes
    }

    /**
     * Uses the HTTP header for content negociation.
     */
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.favorParameter(false)
        configurer.favorPathExtension(false)
    }

    /**
     * ETag support
     */
    @Bean
    fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> {
        val registration = FilterRegistrationBean<ShallowEtagHeaderFilter>()
        registration.filter = ShallowEtagHeaderFilter()
        registration.setDispatcherTypes(DispatcherType.REQUEST, javax.servlet.DispatcherType.ASYNC)
        registration.addUrlPatterns("/*")
        return registration
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.clear()
        // Plain text
        converters.add(StringHttpMessageConverter())
        // Documents
        converters.add(DocumentHttpMessageConverter())
        // JSON
        converters.add(ResourceHttpMessageConverter(uriBuilder, securityService, resourceModules))
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/graphql/**")
        registry.addMapping("/rest/**")
        registry.addMapping("/accounts/**")
        registry.addMapping("/admin/**")
        registry.addMapping("/api/**")
        registry.addMapping("/structure/**")
        registry.addMapping("/branches/**")
        registry.addMapping("/events/**")
        registry.addMapping("/info/**")
        registry.addMapping("/properties/**")
        registry.addMapping("/search/**")
        registry.addMapping("/settings/**")
        registry.addMapping("/user/**")
        registry.addMapping("/validation-stamp-filters/**")
    }

}
