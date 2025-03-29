package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.structure.Signature
import org.springframework.stereotype.Component

@Component
class GQLTypeCreation : GQLType {
    override fun getTypeName() = SIGNATURE

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
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

        const val SIGNATURE = "Signature"

        @JvmStatic
        fun getCreationFromSignature(signature: Signature?): Creation {
            var result = Creation()
            if (signature != null) {
                val user = signature.user
                result = result.withUser(user.name)
                result = result.withTime(Time.store(signature.time))
            }
            return result
        }

        @JvmStatic
        inline fun <reified T> dataFetcher(noinline signatureGetter: (T) -> Signature?) =
                DataFetcher { environment ->
                    val source: Any = environment.getSource()!!
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