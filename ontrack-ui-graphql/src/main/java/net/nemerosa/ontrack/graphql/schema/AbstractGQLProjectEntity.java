package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.TypeResolverProxy;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.model.structure.User;

import java.util.*;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public abstract class AbstractGQLProjectEntity<T extends ProjectEntity> implements GQLType {

    public static final String PROJECT_ENTITY = "ProjectEntity";

    private final Class<T> projectEntityClass;
    private final ProjectEntityType projectEntityType;
    private final List<GQLProjectEntityFieldContributor> projectEntityFieldContributors;

    public AbstractGQLProjectEntity(
            Class<T> projectEntityClass,
            ProjectEntityType projectEntityType,
            List<GQLProjectEntityFieldContributor> projectEntityFieldContributors) {
        this.projectEntityClass = projectEntityClass;
        this.projectEntityType = projectEntityType;
        this.projectEntityFieldContributors = projectEntityFieldContributors;
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
                                .type(
                                        newObject()
                                                .name("Signature")
                                                .field(
                                                        newFieldDefinition()
                                                                .name("user")
                                                                .description("User name")
                                                                .type(GraphQLString)
                                                                .build()
                                                )
                                                .field(
                                                        newFieldDefinition()
                                                                .name("time")
                                                                .description("ISO timestamp")
                                                                .type(GraphQLString)
                                                                .build()
                                                )
                                                .build()
                                )
                                .dataFetcher(creationFetcher())
                                .build()
                )
        );
    }

    protected DataFetcher creationFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (projectEntityClass.isInstance(source)) {
                @SuppressWarnings("unchecked")
                T entity = (T) source;
                return getSignature(entity)
                        .map(AbstractGQLProjectEntity::getMapFromSignature).orElse(null);
            } else {
                return null;
            }
        };
    }

    public static Map<String, String> getMapFromSignature(Signature signature) {
        Map<String, String> map = new LinkedHashMap<>();
        if (signature != null) {
            User user = signature.getUser();
            if (user != null && user.getName() != null) {
                map.put("user", user.getName());
            }
            if (signature.getTime() != null) {
                map.put("time", Time.forStorage(signature.getTime()));
            }
        }
        return map;
    }

    protected abstract Optional<Signature> getSignature(T entity);

}
