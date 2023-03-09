package net.nemerosa.ontrack.extension.av.graphql

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditEntryState
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeCreation
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningAuditEntryState(
    private val creation: GQLTypeCreation,
    private val gqlEnumAutoVersioningAuditState: GQLEnumAutoVersioningAuditState,
) : GQLType {

    override fun getTypeName(): String = AutoVersioningAuditEntryState::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Audit entry for an auto versioning order")
            .field {
                it.name("creation")
                    .type(creation.typeRef.toNotNull())
                    .dataFetcher { env ->
                        val item: AutoVersioningAuditEntryState = env.getSource()
                        val signature = item.signature
                        GQLTypeCreation.getCreationFromSignature(signature)
                    }
            }
            .field {
                it.name("state")
                    .description("State of the processing")
                    .type(gqlEnumAutoVersioningAuditState.getTypeRef().toNotNull())
            }
            .field {
                it.name("running")
                    .description("Is the state indicating a running request?")
                    .type(GraphQLBoolean.toNotNull())
                    .dataFetcher { env ->
                        val item: AutoVersioningAuditEntryState = env.getSource()
                        item.state.isRunning
                    }
            }
            .field {
                it.name("processing")
                    .description("Is the state indicating a request being processed?")
                    .type(GraphQLBoolean.toNotNull())
                    .dataFetcher { env ->
                        val item: AutoVersioningAuditEntryState = env.getSource()
                        item.state.isProcessing
                    }
            }
            .field {
                it.name("data")
                    .description("Associated data")
                    .type(GQLScalarJSON.INSTANCE.toNotNull())
                    .dataFetcher { env ->
                        val item: AutoVersioningAuditEntryState = env.getSource()
                        item.data.asJson()
                    }
            }
            .build()
}