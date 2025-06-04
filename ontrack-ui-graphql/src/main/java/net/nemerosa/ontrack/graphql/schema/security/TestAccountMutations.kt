package net.nemerosa.ontrack.graphql.schema.security

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountInput
import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.DEV)
class TestAccountMutations(
    private val accountService: AccountService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(

        /**
         * Creating an account (for testing)
         */
        simpleMutation(
            name = "createTestAccount",
            description = "Creates a test account (not available in production mode)",
            input = CreateTestAccountInput::class,
            outputName = "account",
            outputType = Account::class,
            outputDescription = "Creates account"
        ) { input ->
            accountService.create(
                AccountInput(
                    email = input.email,
                    fullName = input.fullName,
                    groups = input.groups,
                )
            )
        },
    )
}

data class CreateTestAccountInput(
    @get:NotNull(message = "The account email is required.")
    @get:Size(min = 1, max = 100, message = "The account email must be between 1 and 200 long.")
    @APIDescription("Email of the account")
    val email: String,
    @get:NotNull(message = "The account full name is required.")
    @get:Size(min = 1, max = 100, message = "The account full name must be between 1 and 100 long.")
    @APIDescription("Full name for the account. If null, the full name is not changed.")
    val fullName: String,
    @APIDescription("List of groups the account is a member of. If null, no group is assigned.")
    @ListRef
    val groups: List<Int>? = null,
)
