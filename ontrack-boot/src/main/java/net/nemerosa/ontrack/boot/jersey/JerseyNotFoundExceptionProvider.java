package net.nemerosa.ontrack.boot.jersey;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JerseyNotFoundExceptionProvider implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).
                entity(exception.getMessage()).
                type(MediaType.TEXT_PLAIN).
                build();
    }
}
