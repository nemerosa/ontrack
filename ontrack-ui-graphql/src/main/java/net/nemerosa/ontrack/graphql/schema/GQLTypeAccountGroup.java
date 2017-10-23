package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupMappingService;
import net.nemerosa.ontrack.model.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

/**
 * @see AccountGroup
 */
@Component
public class GQLTypeAccountGroup implements GQLType {

    public static final String ACCOUNT_GROUP = "AccountGroup";

    public static final String ACCOUNTS_FIELD = "accounts";

    private final AccountService accountService;
    private final AccountGroupMappingService accountGroupMappingService;
    private final GQLTypeGlobalRole globalRole;
    private final GQLTypeAuthorizedProject authorizedProject;
    private final GQLTypeAccountGroupMapping accountGroupMapping;

    @Autowired
    public GQLTypeAccountGroup(AccountService accountService,
                               AccountGroupMappingService accountGroupMappingService,
                               GQLTypeGlobalRole globalRole,
                               GQLTypeAuthorizedProject authorizedProject,
                               GQLTypeAccountGroupMapping accountGroupMapping
    ) {
        this.accountService = accountService;
        this.accountGroupMappingService = accountGroupMappingService;
        this.globalRole = globalRole;
        this.authorizedProject = authorizedProject;
        this.accountGroupMapping = accountGroupMapping;
    }

    @Override
    public GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(ACCOUNT_GROUP);
    }

    @Override
    public GraphQLObjectType createType() {
        return newObject()
                .name(ACCOUNT_GROUP)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                // Associated accounts
                .field(field -> field.name(ACCOUNTS_FIELD)
                        .description("List of associated accounts")
                        .type(stdList(new GraphQLTypeReference(GQLTypeAccount.ACCOUNT)))
                        .dataFetcher(fetcher(AccountGroup.class, this::getAccountsForGroup))
                )
                // Global role
                .field(field -> field.name("globalRole")
                        .description("Global role for the account group")
                        .type(globalRole.getTypeRef())
                        .dataFetcher(fetcher(
                                AccountGroup.class,
                                group -> accountService.getGlobalRoleForAccountGroup(group).orElse(null)
                        ))
                )
                // Authorised projects
                .field(field -> field.name("authorizedProjects")
                        .description("List of authorized projects")
                        .type(stdList(authorizedProject.getTypeRef()))
                        .dataFetcher(fetcher(
                                AccountGroup.class,
                                accountService::getProjectPermissionsForAccountGroup
                        ))
                )
                // Mappings
                .field(field -> field.name("mappings")
                        .description("Mappings for this group")
                        .type(stdList(accountGroupMapping.getTypeRef()))
                        .dataFetcher(fetcher(
                                AccountGroup.class,
                                accountGroupMappingService::getMappingsForGroup
                        ))
                )
                // OK
                .build();
    }

    private List<Account> getAccountsForGroup(AccountGroup accountGroup) {
        return accountService.getAccountsForGroup(accountGroup);
    }
}
