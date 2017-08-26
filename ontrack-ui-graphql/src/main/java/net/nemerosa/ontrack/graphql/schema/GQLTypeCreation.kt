package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.structure.Signature
import org.springframework.stereotype.Component

@Component
class GQLTypeCreation : GQLType {
    override fun getType(): GraphQLObjectType =
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
    }

    data class Creation(
            val user: String? = null,
            val time: String? = null
    ) {
        fun withUser(v: String) = Creation(v, time)
        fun withTime(v: String) = Creation(user, v)
    }
}