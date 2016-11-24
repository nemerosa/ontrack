package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeProject extends AbstractGQLProjectEntity<Project> {

    public static final String PROJECT = "Project";

    private final StructureService structureService;
    private final GQLTypeBranch branch;

    @Autowired
    public GQLTypeProject(URIBuilder uriBuilder,
                          SecurityService securityService,
                          List<ResourceDecorator<?>> decorators,
                          StructureService structureService, GQLTypeBranch branch) {
        super(uriBuilder, securityService, Project.class, decorators);
        this.structureService = structureService;
        this.branch = branch;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(PROJECT)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                .field(GraphqlUtils.disabledField())
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(stdList(branch.getType()))
                                .argument(
                                        newArgument()
                                                .name("name")
                                                .description("Exact name of the branch to look for.")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .dataFetcher(projectBranchesFetcher())
                                .build()
                )
                // TODO Events: project creation
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
                } else {
                    return structureService.getBranchesForProject(
                            project.getId()
                    );
                }
            } else {
                return Collections.emptyList();
            }
        };
    }

}
