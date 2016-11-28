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

import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.fetcher;

@Component
public class GQLTypeAccount extends AbstractGQLType {

    public static final String ACCOUNT = "Account";

    private final GQLTypeGlobalRole globalRole;

    @Autowired
    public GQLTypeAccount(URIBuilder uriBuilder, SecurityService securityService) {
        super(uriBuilder, securityService);
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
                                .name("globalRoles")
                                .description("List of global permissions")
                                .type(GraphqlUtils.stdList(globalRole.getType()))
                                .dataFetcher(accountGlobalRolesFetchers())
                                .build()
                )
                .build();
    }

    private DataFetcher accountGlobalRolesFetchers() {
        return fetcher(Account.class, (Account account) -> accountService.getGlobalPermissions().stream()
                .filter(gp -> (gp.getTarget().getType() == PermissionTargetType.ACCOUNT) &&
                        (gp.getTarget().getId() == account.id()))
                .map(GlobalPermission::getRole)
                .collect(Collectors.toList()));
    }

    private DataFetcher accountAccountGroupsFetcher() {
        return fetcher(Account.class, Account::getAccountGroups);
    }

}
