package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ResourceObjectMapper {

    ObjectMapper getObjectMapper();

    ResourceContext getResourceContext();

}
