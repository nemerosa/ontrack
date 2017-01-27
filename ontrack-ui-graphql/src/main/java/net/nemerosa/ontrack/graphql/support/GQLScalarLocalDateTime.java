package net.nemerosa.ontrack.graphql.support;

import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import net.nemerosa.ontrack.common.Time;

import java.time.LocalDateTime;

/**
 * JSON scalar type.
 */
public final class GQLScalarLocalDateTime extends GraphQLScalarType {

    public static final GraphQLScalarType INSTANCE = new GQLScalarLocalDateTime();

    private GQLScalarLocalDateTime() {
        super(
                "LocalDateTime",
                "Local Date Time",
                new Coercing() {

                    @Override
                    public String serialize(Object input) {
                        if (input instanceof LocalDateTime) {
                            return Time.forStorage((LocalDateTime) input);
                        } else if (input instanceof String) {
                            return (String) input;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) {
                        if (input instanceof LocalDateTime) {
                            return (LocalDateTime) input;
                        } else if (input instanceof String) {
                            return Time.fromStorage((String) input);
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public Object parseLiteral(Object input) {
                        return parseValue(input);
                    }
                }
        );
    }
}
