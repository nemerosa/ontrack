package net.nemerosa.ontrack.graphql.support;

import graphql.schema.*;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

public final class GraphqlUtils {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DISABLED = "disabled";
    public static final String DESCRIPTION = "description";

    public static GraphQLFieldDefinition idField() {
        return newFieldDefinition()
                .name(ID)
                .type(new GraphQLNonNull(GraphQLInt))
                .dataFetcher(environment -> {
                    Object source = environment.getSource();
                    if (source instanceof Entity) {
                        ID id = ((Entity) source).getId();
                        return id != null ? id.get() : null;
                    } else {
                        return null;
                    }
                })
                .build();
    }

    public static GraphQLFieldDefinition nameField() {
        return nameField("");
    }

    public static GraphQLFieldDefinition nameField(String description) {
        return newFieldDefinition()
                .name(NAME)
                .description(description)
                .type(new GraphQLNonNull(GraphQLString))
                .build();
    }

    public static GraphQLFieldDefinition stringField(String name, String description) {
        return newFieldDefinition()
                .name(name)
                .description(description)
                .type(GraphQLString)
                .build();
    }

    public static GraphQLFieldDefinition disabledField() {
        return newFieldDefinition()
                .name(DISABLED)
                .type(new GraphQLNonNull(GraphQLBoolean))
                .build();
    }

    public static GraphQLFieldDefinition descriptionField() {
        return newFieldDefinition()
                .name(DESCRIPTION)
                .type(GraphQLString)
                .build();
    }

    public static <E extends Enum<E>> GraphQLOutputType newEnumType(Class<E> enumClass) {
        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum()
                .name(enumClass.getSimpleName());
        for (E e : EnumUtils.getEnumList(enumClass)) {
            builder = builder.value(e.name(), e);
        }
        return builder.build();
    }

    /**
     * Returns a non-null list of non-null types
     */
    public static GraphQLOutputType stdList(GraphQLType type) {
        return new GraphQLNonNull(
                new GraphQLList(
                        new GraphQLNonNull(
                                type
                        )
                )
        );
    }

    public static GraphQLOutputType connectionList(GraphQLObjectType type) {
        return new GraphQLNonNull(
                Relay.connectionType(
                        type.getName() + "Connection",
                        Relay.edgeType(
                                type.getName() + "Edge",
                                type,
                                Collections.emptyList()
                        ),
                        Collections.emptyList()
                )
        );
    }

    public static OptionalInt getIntArgument(DataFetchingEnvironment environment, @SuppressWarnings("SameParameterValue") String name) {
        Object value = environment.getArgument(name);
        if (value instanceof Integer) {
            return OptionalInt.of((Integer) value);
        } else {
            return OptionalInt.empty();
        }
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

    public static <T, R> DataFetcher fetcher(Class<T> sourceType, Function<T, R> fn) {
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

    public static String lowerCamelCase(String text) {
        if (text == null) {
            return null;
        } else if (StringUtils.isBlank(text)) {
            return "";
        } else {
            return StringUtils.uncapitalize(
                    StringUtils.remove(
                            WordUtils.capitalizeFully(
                                    StringUtils.replacePattern(
                                            text,
                                            "[^A-Za-z0-9]",
                                            " "
                                    ),
                                    ' '),
                            " ")
            );
        }
    }
}
