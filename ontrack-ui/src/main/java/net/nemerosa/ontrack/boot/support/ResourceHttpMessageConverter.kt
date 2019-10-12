package net.nemerosa.ontrack.boot.support

import com.fasterxml.jackson.databind.ObjectMapper
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext
import net.nemerosa.ontrack.ui.resource.ResourceModule
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapperFactory
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import java.nio.charset.Charset

class ResourceHttpMessageConverter(uriBuilder: URIBuilder, securityService: SecurityService, resourceModules: List<ResourceModule>) : AbstractHttpMessageConverter<Any>(MediaType("application", "json", DEFAULT_CHARSET), MediaType("application", "*+json", DEFAULT_CHARSET)) {

    private val resourceObjectMapper: ResourceObjectMapper
    private val mapper: ObjectMapper

    init {
        // Resource context
        val resourceContext = DefaultResourceContext(uriBuilder, securityService)
        // Object mapper
        mapper = ObjectMapperFactory.create()
        // Resource mapper
        resourceObjectMapper = ResourceObjectMapperFactory(mapper).resourceObjectMapper(
                resourceModules,
                resourceContext
        )
    }

    override fun supports(clazz: Class<*>): Boolean {
        return true
    }

    override fun readInternal(clazz: Class<*>, inputMessage: HttpInputMessage): Any {
        return mapper.readValue(inputMessage.body, clazz)
    }

    public override fun writeInternal(`object`: Any, outputMessage: HttpOutputMessage) {
        val body = outputMessage.body
        resourceObjectMapper.write(body, `object`)
        body.flush()
    }

    companion object {
        val DEFAULT_CHARSET: Charset = Charsets.UTF_8
    }

}
