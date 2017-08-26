package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.*;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.Signature;

import java.util.*;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

public abstract class AbstractGQLProjectEntity<T extends ProjectEntity> implements GQLType {

    public static final String PROJECT_ENTITY = "ProjectEntity";

    private final Class<T> projectEntityClass;
    private final ProjectEntityType projectEntityType;
    private final List<GQLProjectEntityFieldContributor> projectEntityFieldContributors;
    private final GQLTypeCreation creation;

    public AbstractGQLProjectEntity(
            Class<T> projectEntityClass,
            ProjectEntityType projectEntityType,
            List<GQLProjectEntityFieldContributor> projectEntityFieldContributors,
            GQLTypeCreation creation
    ) {
        this.projectEntityClass = projectEntityClass;
        this.projectEntityType = projectEntityType;
        this.projectEntityFieldContributors = projectEntityFieldContributors;
        this.creation = creation;
    }

    protected GraphQLInterfaceType projectEntityInterface() {
        return GraphQLInterfaceType.newInterface()
                .name(PROJECT_ENTITY)
                // Common fields
                .fields(baseProjectEntityInterfaceFields())
                // TODO Type resolver not set, but it should
                .typeResolver(new TypeResolverProxy())
                // OK
                .build();
    }

    protected List<GraphQLFieldDefinition> projectEntityInterfaceFields() {
        List<GraphQLFieldDefinition> definitions = baseProjectEntityInterfaceFields();
        // For all contributors
        definitions.addAll(
                projectEntityFieldContributors.stream()
                        .map(contributor -> contributor.getFields(projectEntityClass, projectEntityType))
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        // OK
        return definitions;
    }

    private List<GraphQLFieldDefinition> baseProjectEntityInterfaceFields() {
        return new ArrayList<>(
                Arrays.asList(
                        GraphqlUtils.idField(),
                        GraphqlUtils.nameField(),
                        GraphqlUtils.descriptionField(),
                        newFieldDefinition()
                                .name("creation")
                                .type(creation.getType())
                                .dataFetcher(creationFetcher())
                                .build()
                )
        );
    }

    protected DataFetcher creationFetcher() {
        return GraphqlUtils.fetcher(
                projectEntityClass,
                entity -> getSignature(entity)
                        .map(GQLTypeCreation::getCreationFromSignature)
                        .orElse(null)
        );
    }

    protected abstract Optional<Signature> getSignature(T entity);

}
