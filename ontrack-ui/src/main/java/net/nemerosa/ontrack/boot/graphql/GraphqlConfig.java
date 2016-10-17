package net.nemerosa.ontrack.boot.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import net.nemerosa.ontrack.model.structure.BranchType;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

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
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                .field(GraphqlUtils.disabledField())
                .field(
                        newFieldDefinition()
                                .name("type")
                                .type(GraphqlUtils.newEnumType(BranchType.class))
                                .build()
                )
                // TODO Branch properties
                // OK
                .build();
    }


    private GraphQLObjectType projectType() {
        return newObject()
                .name(PROJECT)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                .field(GraphqlUtils.disabledField())
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(GraphqlUtils.stdList(branchType()))
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
                                .name("projects")
                                .type(GraphqlUtils.stdList(projectType()))
                                .argument(
                                        GraphQLArgument.newArgument()
                                                .name("id")
                                                .type(GraphQLInt)
                                                .build()
                                )
                                .dataFetcher(projectFetcher())
                                .build()
                )
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(GraphqlUtils.stdList(branchType()))
                                // TODO Branch search
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
        return environment -> {
            Integer id = environment.getArgument("id");
            if (id != null) {
                // TODO No other argument is expected
                // Fetch by ID
                Project project = structureService.getProject(ID.of(id));
                // TODO Do we catch a project not found? See GraphQL doc for recommendation
                // As list
                return Collections.singletonList(project);
            }
            // TODO Other criterias
            // Whole list
            else {
                return structureService.getProjectList();
            }
        };
    }

}
