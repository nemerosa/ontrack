package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.boot.resources.CoreResourceModule;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.*;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResourceHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final ResourceObjectMapper resourceObjectMapper;
    @SuppressWarnings("UnusedDeclaration")
    private final String jsonPrefix;
    private final ObjectMapper mapper;

    public ResourceHttpMessageConverter(URIBuilder uriBuilder, SecurityService securityService) {
        jsonPrefix = "{} && ";
        // Resource context
        ResourceContext resourceContext = new DefaultResourceContext(uriBuilder, securityService);
        // Registration of modules
        List<ResourceModule> resourceModules = Arrays.asList(
                new CoreResourceModule()
                // TODO Takes extensions into account
        );
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
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        // TODO JSON prefix
        resourceObjectMapper.write(outputMessage.getBody(), object);

    }

}
