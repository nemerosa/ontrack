package net.nemerosa.ontrack.boot.support

import jakarta.servlet.DispatcherType
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.filter.ShallowEtagHeaderFilter
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    /**
     * Uses the HTTP header for content negociation.
     */
    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.favorParameter(false)
    }

    /**
     * ETag support
     */
    @Bean
    fun shallowEtagHeaderFilter(): FilterRegistrationBean<ShallowEtagHeaderFilter> {
        val registration = FilterRegistrationBean<ShallowEtagHeaderFilter>()
        registration.filter = ShallowEtagHeaderFilter()
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC)
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
        converters.add(MappingJackson2HttpMessageConverter())
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        listOf(
            "/graphql/**",
            "/rest/**",
            "/extension/**",
            "/hook/secured/**",
        ).forEach {
            registry.addMapping(it).allowedMethods(*ALLOWED_API_METHODS.toTypedArray())
        }
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/ui").setViewName("forward:/ui/index.html")
    }

    companion object {
        private val ALLOWED_API_METHODS = setOf("GET", "POST", "PUT", "DELETE", "HEAD")
    }

}
