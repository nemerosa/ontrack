package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.nemerosa.ontrack.boot.resources.ResourceModule;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.JsonViewClass;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

class ViewAwareMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private String jsonPrefix;

    public ViewAwareMappingJackson2HttpMessageConverter(URIBuilder uriBuilder) {
        ObjectMapper mapper = ObjectMapperFactory.create();
        mapper.registerModule(new ResourceModule(uriBuilder));
        setObjectMapper(mapper);
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

        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        // The following has been deprecated as late as Jackson 2.2 (April 2013);
        // preserved for the time being, for Jackson 2.0/2.1 compatibility.
        @SuppressWarnings("deprecation")
        JsonGenerator jsonGenerator = getObjectMapper().getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);

        // A workaround for JsonGenerators not applying serialization features
        // https://github.com/FasterXML/jackson-databind/issues/12
        if (getObjectMapper().isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }

        try {
            if (this.jsonPrefix != null) {
                jsonGenerator.writeRaw(this.jsonPrefix);
            }
            Class<?> viewClass = getViewClass(object);
            ObjectWriter writer = getObjectMapper().writerWithView(viewClass);
            writer.writeValue(jsonGenerator, object);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    protected Class<?> getViewClass(Object object) {
        return JsonViewClass.getViewClass(object);
    }
}
