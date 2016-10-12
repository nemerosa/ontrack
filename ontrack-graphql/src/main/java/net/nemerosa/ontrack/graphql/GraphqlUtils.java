package net.nemerosa.ontrack.graphql;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

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

}
