package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.structure.Signature
import org.springframework.stereotype.Component

@Component
class GQLTypeCreation : GQLType {
    override fun getTypeRef() = GraphQLTypeReference(SIGNATURE)

    override fun createType(): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(SIGNATURE)
                    .field {
                        it.name("user")
                                .description("User name")
                                .type(Scalars.GraphQLString)
                    }
                    .field {
                        it.name("time")
                                .description("ISO timestamp")
                                .type(Scalars.GraphQLString)
                    }
                    .build()

    companion object {

        @JvmField
        val SIGNATURE = "Signature"

        @JvmStatic
        fun getCreationFromSignature(signature: Signature?): Creation {
            var result = Creation()
            if (signature != null) {
                val user = signature.user
                if (user != null && user.name != null) {
                    result = result.withUser(user.name)
                }
                if (signature.time != null) {
                    result = result.withTime(Time.forStorage(signature.time))
                }
            }
            return result
        }

        @JvmStatic
        inline fun <reified T> dataFetcher(noinline signatureGetter: (T) -> Signature?) =
                DataFetcher { environment ->
                    val source: Any = environment.getSource()
                    if (source is T) {
                        signatureGetter(source)?.let { getCreationFromSignature(it) }
                    } else {
                        throw IllegalStateException("Fetcher source not an ${T::class.qualifiedName}")
                    }
                }
    }

    data class Creation(
            val user: String? = null,
            val time: String? = null
    ) {
        fun withUser(v: String) = Creation(v, time)
        fun withTime(v: String) = Creation(user, v)
    }
}