package net.nemerosa.ontrack.graphql;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Configuration
public class GraphqlConfig {

    public static final String QUERY = "Query";
    public static final String PROJECT = "Project";

    /**
     * GraphQL schema definition
     */
    @Bean
    @Qualifier("ontrack")
    public GraphQLSchema grapqlSchema() {
        return GraphQLSchema.newSchema()
                .query(queryType())
                .build();
    }

    private GraphQLObjectType projectType() {
        return newObject()
                .name(PROJECT)
                .field(
                        newFieldDefinition()
                                .name("name")
                                .type(new GraphQLNonNull(GraphQLString))
                                .build()
                )
                // TODO Branches
                // TODO Project properties
                // OK
                .build();
    }

    private GraphQLObjectType queryType() {
        return newObject()
                .name(QUERY)
                // Project
                .field(
                        newFieldDefinition()
                                .name("project")
                                .type(projectType())
                                .build()
                )
                // TODO Extension contributions
                // OK
                .build();
    }

}
