package net.nemerosa.ontrack.boot.jersey;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import net.nemerosa.ontrack.json.ObjectMapperFactory;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class JerseyJacksonFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {

        context.register(new JacksonJaxbJsonProvider(
                ObjectMapperFactory.create(),
                JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS
        ));

        return true;
    }

}
