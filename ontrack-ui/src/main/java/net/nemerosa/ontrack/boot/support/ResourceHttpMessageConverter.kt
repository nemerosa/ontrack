package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

public class ResourceHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final ResourceObjectMapper resourceObjectMapper;
    private final ObjectMapper mapper;

    public ResourceHttpMessageConverter(URIBuilder uriBuilder, SecurityService securityService, List<ResourceModule> resourceModules) {
        super(
                new MediaType("application", "json", DEFAULT_CHARSET),
                new MediaType("application", "*+json", DEFAULT_CHARSET)
        );
        // Resource context
        ResourceContext resourceContext = new DefaultResourceContext(uriBuilder, securityService);
        // Object mapper
        mapper = ObjectMapperFactory.create();
        // Resource mapper
        resourceObjectMapper = new ResourceObjectMapperFactory(mapper).resourceObjectMapper(
                resourceModules,
                resourceContext
        );
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return mapper.readValue(inputMessage.getBody(), clazz);
    }

    @Override
    protected void writeInternal(@NotNull Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream body = outputMessage.getBody();
        resourceObjectMapper.write(body, object);
        body.flush();
    }

}
