package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountService;
import net.nemerosa.ontrack.model.security.AuthenticatedAccount;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.fetcher;

@Component
public class GQLTypeAccount extends AbstractGQLType {

    public static final String ACCOUNT = "Account";

    private final AccountService accountService;

    @Autowired
    public GQLTypeAccount(URIBuilder uriBuilder, SecurityService securityService, AccountService accountService) {
        super(uriBuilder, securityService);
        this.accountService = accountService;
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
                                .name("actualGroups")
                                .description("List of groups gotten at runtime")
                                .type(GraphqlUtils.stdList(new GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP)))
                                .dataFetcher(accountActualGroupsFetcher())
                                .build()
                )
                .build();
    }

    private DataFetcher accountActualGroupsFetcher() {
        return fetcher(Account.class, account -> accountService.withACL(AuthenticatedAccount.of(account)));
    }

    private DataFetcher accountAccountGroupsFetcher() {
        return fetcher(Account.class, Account::getAccountGroups);
    }

}
