package net.nemerosa.ontrack.graphql.support;

import graphql.schema.*;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

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
    public static final String DISABLED = "disabled";
    public static final String DESCRIPTION = "description";
    public static final String STD_LIST_ARG_FIRST = "first";
    public static final String STD_LIST_ARG_LAST = "last";

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

    public static List<GraphQLArgument> stdListArguments() {
        return Arrays.asList(
                GraphQLArgument.newArgument()
                        .name(STD_LIST_ARG_FIRST)
                        .description("Number of items to return from the beginning of the list")
                        .type(GraphQLInt)
                        .build(),
                GraphQLArgument.newArgument()
                        .name(STD_LIST_ARG_LAST)
                        .description("Number of items to return from the end of the list")
                        .type(GraphQLInt)
                        .build()
        );
    }

    public static <T> List<T> stdListArgumentsFilter(List<T> list, DataFetchingEnvironment environment) {
        OptionalInt first = getIntArgument(environment, STD_LIST_ARG_FIRST);
        OptionalInt last = getIntArgument(environment, STD_LIST_ARG_LAST);
        if (first.isPresent()) {
            if (last.isPresent()) {
                throw new IllegalStateException(
                        String.format(
                                "Only one of `%s` or `%s` is expected as argument",
                                STD_LIST_ARG_FIRST,
                                STD_LIST_ARG_LAST
                        )
                );
            } else {
                // First items...
                return list.subList(0, Math.min(list.size(), first.getAsInt()));
            }
        } else if (last.isPresent()) {
            // Last items
            return list.subList(Math.max(0, list.size() - last.getAsInt()), list.size());
        } else {
            // No range
            return list;
        }
    }
}
