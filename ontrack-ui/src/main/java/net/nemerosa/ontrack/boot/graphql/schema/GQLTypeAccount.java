package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.fetcher;

@Component
public class GQLTypeAccount implements GQLType {

    public static final String ACCOUNT = "Account";

    private final AccountService accountService;
    private final GQLTypeGlobalRole globalRole;
    private final GQLTypeAuthorizedProject authorizedProject;

    @Autowired
    public GQLTypeAccount(AccountService accountService, GQLTypeGlobalRole globalRole, GQLTypeAuthorizedProject authorizedProject) {
        this.accountService = accountService;
        this.globalRole = globalRole;
        this.authorizedProject = authorizedProject;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(ACCOUNT)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField("Unique name for the account"))
                .field(GraphqlUtils.stringField("fullName", "Full name of the account"))
                .field(GraphqlUtils.stringField("email", "Email of the account"))
                .field(
                        newFieldDefinition()
                                .name("authenticationSource")
                                .description("Source of authentication (builtin, ldap, etc.)")
                                .type(GraphQLString)
                                .dataFetcher(environment -> ((Account) environment.getSource()).getAuthenticationSource().getId())
                                .build()
                )
                .field(GraphqlUtils.stringField("role", "Security role (admin or none)"))
                .field(
                        newFieldDefinition()
                                .name("groups")
                                .description("List of groups the account belongs to")
                                .type(GraphqlUtils.stdList(new GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP)))
                                .dataFetcher(accountAccountGroupsFetcher())
                                .build()
                )
                .field(
                        newFieldDefinition()
                                .name("globalRole")
                                .description("Global role for the account")
                                .type(globalRole.getType())
                                .dataFetcher(accountGlobalRoleFetcher())
                                .build()
                )
                .field(
                        newFieldDefinition()
                                .name("authorizedProjects")
                                .description("List of authorized projects")
                                .type(GraphqlUtils.stdList(authorizedProject.getType()))
                                .dataFetcher(accountAuthorizedProjectsFetcher())
                                .build()
                )
                .build();
    }

    private DataFetcher accountAuthorizedProjectsFetcher() {
        return fetcher(Account.class, accountService::getProjectPermissionsForAccount);
    }

    private DataFetcher accountGlobalRoleFetcher() {
        return fetcher(
                Account.class,
                account -> accountService.getGlobalRoleForAccount(account).orElse(null)
        );
    }

    private DataFetcher accountAccountGroupsFetcher() {
        return fetcher(Account.class, Account::getAccountGroups);
    }

}
