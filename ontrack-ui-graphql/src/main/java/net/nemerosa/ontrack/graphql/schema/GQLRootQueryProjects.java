package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.checkArgList;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryProjects implements GQLRootQuery {

    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";
    public static final String ARG_FAVOURITES = "favourites";

    private final StructureService structureService;
    private final GQLTypeProject project;
    private final GQLInputPropertyFilter propertyFilter;

    @Autowired
    public GQLRootQueryProjects(StructureService structureService, GQLTypeProject project, GQLInputPropertyFilter propertyFilter) {
        this.structureService = structureService;
        this.project = project;
        this.propertyFilter = propertyFilter;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("projects")
                .type(stdList(project.getType()))
                .argument(
                        newArgument()
                                .name(ARG_ID)
                                .description("ID of the project to look for")
                                .type(GraphQLInt)
                                .build()
                )
                .argument(
                        newArgument()
                                .name(ARG_NAME)
                                .description("Name of the project to look for")
                                .type(GraphQLString)
                                .build()
                )
                .argument(a -> a.name(ARG_FAVOURITES)
                        .description("Favourite projects only")
                        .type(GraphQLBoolean)
                )
                .argument(propertyFilter.asArgument())
                .dataFetcher(projectFetcher())
                .build();
    }

    private DataFetcher projectFetcher() {
        return environment -> {
            Integer id = environment.getArgument(ARG_ID);
            String name = environment.getArgument(ARG_NAME);
            boolean favourites = GraphqlUtils.getBooleanArgument(environment, ARG_FAVOURITES, false);
            // Per ID
            if (id != null) {
                // No other argument is expected
                checkArgList(environment, ARG_ID);
                // Fetch by ID
                Project project = structureService.getProject(ID.of(id));
                // As list
                return Collections.singletonList(project);
            }
            // Name
            else if (name != null) {
                // No other argument is expected
                checkArgList(environment, ARG_NAME);
                return structureService.findProjectByName(name)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
            }
            // Favourites
            else if (favourites) {
                // No other argument is expected
                checkArgList(environment, ARG_FAVOURITES);
                return structureService.getProjectFavourites();
            }
            // Other criterias
            else {
                // Filter to use
                Predicate<Project> filter = p -> true;
                // Property filter?
                Object propertyFilterArg = environment.getArgument(GQLInputPropertyFilter.ARGUMENT_NAME);
                if (propertyFilterArg != null) {
                    GQLInputPropertyFilter.PropertyFilter filterObject = propertyFilter.convert(propertyFilterArg);
                    if (filterObject != null && StringUtils.isNotBlank(filterObject.getType())) {
                        filter = filter.and(propertyFilter.getFilter(filterObject));
                    }
                }
                // Whole list
                return structureService.getProjectList()
                        .stream()
                        .filter(filter)
                        .collect(Collectors.toList());
            }
        };
    }
}
