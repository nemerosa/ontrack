package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLTypeProject extends AbstractGQLProjectEntityWithSignature<Project> {

    public static final String PROJECT = "Project";

    private final StructureService structureService;
    private final GQLTypeBranch branch;

    @Autowired
    public GQLTypeProject(URIBuilder uriBuilder,
                          SecurityService securityService,
                          List<ResourceDecorator<?>> decorators,
                          StructureService structureService,
                          GQLTypeBranch branch,
                          PropertyService propertyService,
                          GQLTypeProperty property) {
        super(uriBuilder, securityService, Project.class, ProjectEntityType.PROJECT, decorators, propertyService, property);
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
                                                .description("Regular expression to match against the branch name")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .dataFetcher(projectBranchesFetcher())
                                .build()
                )
                // OK
                .build();

    }

    private DataFetcher projectBranchesFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof Project) {
                Project project = (Project) source;
                String name = environment.getArgument("name");
                // Combined filter
                Predicate<Branch> filter = b -> true;
                // Name criteria
                if (name != null) {
                    Pattern nameFilter = Pattern.compile(name);
                    filter = filter.and(branch -> nameFilter.matcher(branch.getName()).matches());
                }
                return structureService.getBranchesForProject(project.getId()).stream()
                        .filter(filter)
                        .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        };
    }

    @Override
    protected Optional<Signature> getSignature(Project entity) {
        return Optional.ofNullable(entity.getSignature());
    }

}
