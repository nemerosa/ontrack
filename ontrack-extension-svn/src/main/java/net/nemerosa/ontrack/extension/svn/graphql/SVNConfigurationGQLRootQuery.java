package net.nemerosa.ontrack.extension.svn.graphql;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService;
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;

@Component
public class SVNConfigurationGQLRootQuery implements GQLRootQuery {

    private final SVNConfigurationService svnConfigurationService;
    private final SVNConfigurationGQLType svnConfigurationGQLType;

    @Autowired
    public SVNConfigurationGQLRootQuery(SVNConfigurationService svnConfigurationService, SVNConfigurationGQLType svnConfigurationGQLType) {
        this.svnConfigurationService = svnConfigurationService;
        this.svnConfigurationGQLType = svnConfigurationGQLType;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("svnConfigurations")
                .description("List of SVN configurations")
                .type(GraphqlUtils.stdList(svnConfigurationGQLType.getType()))
                .argument(a -> a.name("name")
                        .description("Configuration name")
                        .type(GraphQLString)
                )
                .dataFetcher(this::getSVNConfigurationList)
                .build();
    }

    private List<SVNConfiguration> getSVNConfigurationList(DataFetchingEnvironment environment) {
        Predicate<SVNConfiguration> filter = GraphqlUtils.getStringArgument(environment, "name")
                .map(s -> (Predicate<SVNConfiguration>) c -> StringUtils.equals(s, c.getName()))
                .orElse(c -> true);
        return svnConfigurationService.getConfigurations().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
