package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.nemerosa.ontrack.boot.resources.CoreResourceModule;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.*;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class ResourceHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private final ResourceObjectMapper resourceObjectMapper;
    private String jsonPrefix;

    public ResourceHttpMessageConverter(URIBuilder uriBuilder, SecurityService securityService) {
        // Resource context
        ResourceContext resourceContext = new DefaultResourceContext(uriBuilder, securityService);
        // Registration of modules
        List<ResourceModule> resourceModules = Arrays.asList(
                new CoreResourceModule()
                // TODO Takes extensions into account
        );
        // Object mapper
        ObjectMapper mapper = ObjectMapperFactory.create();
        setObjectMapper(mapper);
        // Resource mapper
        resourceObjectMapper = new ResourceObjectMapperFactory(mapper).resourceObjectMapper(
                resourceModules,
                resourceContext
        );
    }

    @Override
    public void setJsonPrefix(String jsonPrefix) {
        super.setJsonPrefix(jsonPrefix);
        this.jsonPrefix = jsonPrefix;
    }

    @Override
    public void setPrefixJson(boolean prefixJson) {
        super.setPrefixJson(prefixJson);
        this.jsonPrefix = (prefixJson ? "{} && " : null);
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        ObjectMapper mapper = resourceObjectMapper.getObjectMapper();

        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        // The following has been deprecated as late as Jackson 2.2 (April 2013);
        // preserved for the time being, for Jackson 2.0/2.1 compatibility.
        @SuppressWarnings("deprecation")
        JsonGenerator jsonGenerator = mapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);

        // A workaround for JsonGenerators not applying serialization features
        // https://github.com/FasterXML/jackson-databind/issues/12
        if (mapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }

        try {
            if (this.jsonPrefix != null) {
                jsonGenerator.writeRaw(this.jsonPrefix);
            }
            resourceObjectMapper.write(jsonGenerator, object);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

}
