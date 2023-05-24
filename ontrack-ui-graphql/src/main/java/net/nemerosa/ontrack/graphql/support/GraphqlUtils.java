package net.nemerosa.ontrack.graphql.support;

import graphql.schema.*;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

/**
 * @deprecated Use Kotlin-friendly fields
 */
@Deprecated
public final class GraphqlUtils {

    public static final String ID = "id";
    public static final String NAME = "name";

    public static GraphQLFieldDefinition nameField(String description) {
        return newFieldDefinition()
                .name(NAME)
                .description(description)
                .type(GraphQLString)
                .build();
    }

    public static GraphQLFieldDefinition stringField(String name, String description) {
        return newFieldDefinition()
                .name(name)
                .description(description)
                .type(GraphQLString)
                .build();
    }

    /**
     * Returns a non-null list of non-null types
     *
     * @deprecated Use the Kotlin form
     */
    @Deprecated
    public static GraphQLOutputType stdList(GraphQLType type) {
        return new GraphQLNonNull(
                new GraphQLList(
                        new GraphQLNonNull(
                                type
                        )
                )
        );
    }

    public static boolean getBooleanArgument(DataFetchingEnvironment environment, String name, @SuppressWarnings("SameParameterValue") boolean defaultValue) {
        Object value = environment.getArgument(name);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return defaultValue;
        }
    }

    public static Optional<String> getStringArgument(DataFetchingEnvironment environment, String name) {
        Object value = environment.getArgument(name);
        if (value instanceof String) {
            return Optional.of((String) value);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Checks list of arguments
     */
    public static void checkArgList(DataFetchingEnvironment environment, String... args) {
        Set<String> actualArgs = getActualArguments(environment).keySet();
        Set<String> expectedArgs = new HashSet<>(Arrays.asList(args));
        if (!Objects.equals(actualArgs, expectedArgs)) {
            throw new IllegalStateException(
                    String.format(
                            "Expected this list of arguments: %s, but was: %s",
                            expectedArgs,
                            actualArgs
                    )
            );
        }
    }

    private static Map<String, Object> getActualArguments(DataFetchingEnvironment environment) {
        return environment.getArguments().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public static <T, R> DataFetcher<R> fetcher(Class<T> sourceType, Function<T, R> fn) {
        return environment -> {
            Object o = environment.getSource();
            if (sourceType.isInstance(o)) {
                @SuppressWarnings("unchecked") T t = (T) o;
                return fn.apply(t);
            } else {
                return null;
            }
        };
    }

    public static <T, R> DataFetcher<R> fetcher(Class<T> sourceType, BiFunction<DataFetchingEnvironment, T, R> fn) {
        return environment -> {
            Object o = environment.getSource();
            if (sourceType.isInstance(o)) {
                @SuppressWarnings("unchecked") T t = (T) o;
                return fn.apply(environment, t);
            } else {
                return null;
            }
        };
    }

}
