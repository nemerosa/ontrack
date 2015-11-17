package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.*;

import java.beans.ConstructorProperties;

/**
 * This introspector is plugged into a Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} in order
 * to setup artificially a {@link com.fasterxml.jackson.annotation.JsonCreator} on constructors generated
 * by <a href="http://projectlombok.org/">Lombok</a> using the {@link lombok.Data} annotation.
 * <p>
 * It uses the fact that Lombok adds the {@link java.beans.ConstructorProperties} annotations on the generated constructors.
 *
 * TODO Once Jackson 2.7.0 is out, this introspector can be removed (see https://github.com/FasterXML/jackson-databind/issues/905)
 *
 * @see ObjectMapperFactory
 */
public class ConstructorPropertiesAnnotationIntrospector extends NopAnnotationIntrospector {

    @Override
    public boolean hasCreatorAnnotation(Annotated a) {
        /* No dedicated disabling; regular @JsonIgnore used
         * if needs to be ignored (and if so, is handled prior
         * to this method getting called)
         */
        JsonCreator ann = _findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            return (ann.mode() != JsonCreator.Mode.DISABLED);
        }
        if (a instanceof AnnotatedConstructor) {
            ConstructorProperties props = _findAnnotation(a, ConstructorProperties.class);
            // 08-Nov-2015, tatu: One possible check would be to ensure there is at least
            //    one name iff constructor has arguments. But seems unnecessary for now.
            if (props != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        JsonGetter jg = _findAnnotation(a, JsonGetter.class);
        if (jg != null) {
            return PropertyName.construct(jg.value());
        }
        JsonProperty pann = _findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        PropertyName ctorName = _findConstructorName(a);
        if (ctorName != null) {
            return ctorName;
        }
        if (_hasAnnotation(a, JsonSerialize.class)
                || _hasAnnotation(a, JsonView.class)
                || _hasAnnotation(a, JsonRawValue.class)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        // @JsonSetter has precedence over @JsonProperty, being more specific
        // @JsonDeserialize implies that there is a property, but no name
        JsonSetter js = _findAnnotation(a, JsonSetter.class);
        if (js != null) {
            return PropertyName.construct(js.value());
        }
        JsonProperty pann = _findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        PropertyName ctorName = _findConstructorName(a);
        if (ctorName != null) {
            return ctorName;
        }

        /* 22-Apr-2014, tatu: Should figure out a better way to do this, but
         *   it's actually bit tricky to do it more efficiently (meta-annotations
         *   add more lookups; AnnotationMap costs etc)
         */
        if (_hasAnnotation(a, JsonDeserialize.class)
                || _hasAnnotation(a, JsonView.class)
                || _hasAnnotation(a, JsonUnwrapped.class) // [databind#442]
                || _hasAnnotation(a, JsonBackReference.class)
                || _hasAnnotation(a, JsonManagedReference.class)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    protected PropertyName _findConstructorName(Annotated a) {
        if (a instanceof AnnotatedParameter) {
            AnnotatedParameter p = (AnnotatedParameter) a;
            AnnotatedWithParams ctor = p.getOwner();

            if (ctor != null) {
                ConstructorProperties props = _findAnnotation(ctor, ConstructorProperties.class);
                if (props != null) {
                    String[] names = props.value();
                    int ix = p.getIndex();
                    if (ix < names.length) {
                        return PropertyName.construct(names[ix]);
                    }
                }
            }
        }
        return null;
    }
}
