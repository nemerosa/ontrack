package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryProjects implements GQLRootQuery {

    private final StructureService structureService;
    private final GQLProject project;

    @Autowired
    public GQLRootQueryProjects(StructureService structureService, GQLProject project) {
        this.structureService = structureService;
        this.project = project;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("projects")
                .type(stdList(project.getType()))
                .argument(
                        newArgument()
                                .name("id")
                                .description("ID of the project to look for")
                                .type(GraphQLInt)
                                .build()
                )
                .argument(
                        newArgument()
                                .name("name")
                                .description("Name of the project to look for")
                                .type(GraphQLString)
                                .build()
                )
                .dataFetcher(projectFetcher())
                .build();
    }

    private DataFetcher projectFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            String name = environment.getArgument("name");
            // Per ID
            if (id != null) {
                // TODO No other argument is expected
                // Fetch by ID
                Project project = structureService.getProject(ID.of(id));
                // As list
                return Collections.singletonList(project);
            }
            // Name
            else if (name != null) {
                // TODO No other argument is expected
                return structureService.findProjectByName(name)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
            }
            // TODO Other criterias
            // Whole list
            else {
                return structureService.getProjectList();
            }
        };
    }
}
