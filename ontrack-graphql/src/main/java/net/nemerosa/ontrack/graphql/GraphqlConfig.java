package net.nemerosa.ontrack.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import net.nemerosa.ontrack.model.structure.BranchType;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.graphql.GraphqlUtils.*;

@Configuration
public class GraphqlConfig {

    public static final String QUERY = "Query";
    public static final String PROJECT = "Project";
    public static final String BRANCH = "Branch";

    @Autowired
    private StructureService structureService;

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

    private GraphQLObjectType branchType() {
        return newObject()
                .name(BRANCH)
                .field(idField())
                .field(nameField())
                .field(descriptionField())
                .field(disabledField())
                .field(
                        newFieldDefinition()
                                .name("type")
                                .type(newEnumType(BranchType.class))
                                .build()
                )
                // TODO Branch properties
                // OK
                .build();
    }


    private GraphQLObjectType projectType() {
        return newObject()
                .name(PROJECT)
                .field(idField())
                .field(nameField())
                .field(descriptionField())
                .field(disabledField())
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branch")
                                .type(stdList(branchType()))
                                .dataFetcher(projectBranchesFetcher())
                                .build()
                )
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
                                .type(new GraphQLList(projectType()))
                                .dataFetcher(projectFetcher())
                                .build()
                )
                // TODO Extension contributions
                // OK
                .build();
    }

    private DataFetcher projectBranchesFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Project) {
                return structureService.getBranchesForProject(
                        ((Project) source).getId()
                );
            } else {
                return Collections.emptyList();
            }
        };
    }

    private DataFetcher projectFetcher() {
        // TODO Search criterias
        return environment -> structureService.getProjectList();
    }

}
