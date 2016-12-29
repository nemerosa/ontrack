package net.nemerosa.ontrack.boot.graphql.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import net.nemerosa.ontrack.json.JsonParseException;
import net.nemerosa.ontrack.json.ObjectMapperFactory;

import java.io.IOException;

/**
 * JSON scalar type.
 */
public final class GQLScalarJSON extends GraphQLScalarType {

    public static final GraphQLScalarType INSTANCE = new GQLScalarJSON();

    private GQLScalarJSON() {
        super(
                "JSON",
                "Custom JSON value",
                new Coercing() {

                    private final ObjectMapper mapper = ObjectMapperFactory.create();

                    @Override
                    public JsonNode serialize(Object input) {
                        if (input instanceof JsonNode) {
                            return (JsonNode) input;
                        } else if (input instanceof String) {
                            try {
                                return mapper.readTree((String) input);
                            } catch (IOException e) {
                                throw new JsonParseException(e);
                            }
                        } else {
                            return mapper.valueToTree(input);
                        }
                    }

                    @Override
                    public Object parseValue(Object input) {
                        return serialize(input);
                    }

                    @Override
                    public Object parseLiteral(Object input) {
                        if (input instanceof StringValue) {
                            return serialize(input);
                        } else {
                            return null;
                        }
                    }
                }
        );
    }
}
