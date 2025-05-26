package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.GroupMappingService
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.stereotype.Component

@Component
class GroupMappingMutations(
    private val groupMappingService: GroupMappingService,
    private val accountService: AccountService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        unitMutation<MapGroupInput>(
            name = "mapGroup",
            description = "Maps an identity provider group to a Yontrack group",
        ) { input ->
            groupMappingService.mapGroup(
                idpGroup = input.idpGroup,
                accountGroup = input.groupId?.let { accountService.getAccountGroup(ID.of(it)) },
            )
        }
    )
}

data class MapGroupInput(
    val idpGroup: String,
    val groupId: Int?,
)
