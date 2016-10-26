package net.nemerosa.ontrack.boot.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Optional;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Configuration
public class GraphqlConfig {

    public static final String QUERY = "Query";
    public static final String PROJECT = "Project";
    public static final String BRANCH = "Branch";
    public static final String BUILD = "Build";

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

    private GraphQLObjectType buildType() {
        return newObject()
                .name(BUILD)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                // TODO Signature
                // TODO Promotion runs
                // TODO Validation runs
                // TODO Build properties
                // OK
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
                // TODO Events: branch creation
                // TODO Branch properties
                // Builds for the branch
                .field(
                        newFieldDefinition()
                                .name("builds")
                                // TODO Use connectionList
                                .type(GraphqlUtils.stdList(buildType()))
                                // TODO Build fetcher
                                .build()
                )
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
                                .argument(
                                        GraphQLArgument.newArgument()
                                                .name("name")
                                                .description("Exact name of the branch to look for.")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .dataFetcher(projectBranchesFetcher())
                                .build()
                )
                // TODO Events: project creation
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
                Project project = (Project) source;
                String name = environment.getArgument("name");
                // TODO Combined filter
                // TODO Other criterias
                if (name != null) {
                    Optional<Branch> oBranch = structureService.findBranchByName(project.getName(), name);
                    if (oBranch.isPresent()) {
                        return Collections.singletonList(oBranch.get());
                    } else {
                        return Collections.emptyList();
                    }
                }
                else {
                    return structureService.getBranchesForProject(
                            project.getId()
                    );
                }
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
