package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.Collections;

/**
 * This introspector is plugged into a Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} in order
 * to setup artificially a {@link com.fasterxml.jackson.annotation.JsonCreator} on constructors generated
 * by <a href="http://projectlombok.org/">Lombok</a> using the {@link lombok.Data} annotation.
 * <p>
 * It uses the fact that Lombok adds the {@link java.beans.ConstructorProperties} annotations on the generated constructors.
 *
 * @see ObjectMapperFactory
 */
public class ConstructorPropertiesAnnotationIntrospector extends NopAnnotationIntrospector {

    @Override
    public boolean hasCreatorAnnotation(Annotated a) {
        if (!(a instanceof AnnotatedConstructor)) {
            return false;
        }

        AnnotatedConstructor ac = (AnnotatedConstructor) a;

        Constructor<?> c = ac.getAnnotated();
        ConstructorProperties properties = c.getAnnotation(ConstructorProperties.class);

        if (properties == null) {
            return false;
        }

        for (int i = 0; i < ac.getParameterCount(); i++) {
            String name = properties.value()[i];
            JsonProperty jsonProperty =
                    ProxyAnnotation.of(JsonProperty.class, Collections.singletonMap("value", name));
            ac.getParameter(i).addOrOverride(jsonProperty);
        }
        return true;
    }
}
