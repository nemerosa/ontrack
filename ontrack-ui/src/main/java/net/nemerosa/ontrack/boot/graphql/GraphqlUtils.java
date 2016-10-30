package net.nemerosa.ontrack.boot.graphql;

import graphql.schema.*;
import net.nemerosa.ontrack.boot.graphql.relay.Relay;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.apache.commons.lang3.EnumUtils;

import java.util.Collections;
import java.util.OptionalInt;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

public final class GraphqlUtils {

    public static GraphQLFieldDefinition idField() {
        return newFieldDefinition()
                .name("id")
                .type(new GraphQLNonNull(GraphQLInt))
                .dataFetcher(environment -> {
                    Object source = environment.getSource();
                    if (source instanceof ProjectEntity) {
                        ID id = ((ProjectEntity) source).getId();
                        return id != null ? id.get() : null;
                    } else {
                        return null;
                    }
                })
                .build();
    }

    public static GraphQLFieldDefinition nameField() {
        return newFieldDefinition()
                .name("name")
                .type(new GraphQLNonNull(GraphQLString))
                .build();
    }

    public static GraphQLFieldDefinition disabledField() {
        return newFieldDefinition()
                .name("disabled")
                .type(new GraphQLNonNull(GraphQLBoolean))
                .build();
    }

    public static GraphQLFieldDefinition descriptionField() {
        return newFieldDefinition()
                .name("description")
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
    public static GraphQLOutputType stdList(GraphQLObjectType type) {
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
                                null,
                                Collections.emptyList()
                        ),
                        Collections.emptyList()
                )
        );
    }

    public static OptionalInt getIntArgument(DataFetchingEnvironment environment, String name) {
        Object value = environment.getArgument(name);
        if (value instanceof Integer) {
            return OptionalInt.of((Integer) value);
        } else {
            return OptionalInt.empty();
        }
    }
}
