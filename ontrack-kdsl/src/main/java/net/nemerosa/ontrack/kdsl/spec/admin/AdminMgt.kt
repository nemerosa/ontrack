package net.nemerosa.ontrack.kdsl.spec.admin

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.checkData
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateUserMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.GrantGlobalRoleToAccountMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.connector.parse
import java.net.URLEncoder

/**
 * Admin interface to Ontrack.
 */
class AdminMgt(connector: Connector) : Connected(connector) {

    /**
     * Gets a list of log entries
     *
     * @param text Filter on text message
     * @param count Number of entries to return
     */
    fun logEntries(text: String = "", count: Int = 1): List<LogEntry> =
        connector.get("/rest/admin/logs?count=$count&text=${URLEncoder.encode(text, "UTF-8")}")
            .body
            .parse<LogEntries>()
            .resources

    /**
     * Mgt. of predefined promotion levels
     */
    val predefinedPromotionLevels: PredefinedPromotionLevelsMgt by lazy {
        PredefinedPromotionLevelsMgt(connector)
    }

    /**
     * Creating a user
     */
    fun createUser(
        name: String,
        fullName: String = name,
        email: String = "$name@test.com",
    ): Account =
        graphqlConnector.mutate(
            CreateUserMutation(
                name,
                fullName,
                email,
            )
        ) { it?.createBuiltInAccount?.payloadUserErrors?.convert() }
            ?.checkData { it.createBuiltInAccount?.account }
            ?.run {
                Account(
                    id = id.toInt(),
                    name = name,
                )
            }
            ?: error("could not create user")

    fun grantGlobalRoleToAccount(account: Account, globalRole: String) {
        graphqlConnector.mutate(
            GrantGlobalRoleToAccountMutation(
                account.id,
                globalRole
            )
        ) { it?.grantGlobalRoleToAccount?.payloadUserErrors?.convert() }
    }

}