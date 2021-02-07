package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL("OIDC configuration")
class OidcSettings {

    private final Ontrack ontrack

    OidcSettings(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    @DSLMethod(value = "Creates an OIDC provider", count = 7)
    void createProvider(
            String id,
            String name,
            String description,
            String issuerId,
            String clientId,
            String clientSecret,
            String groupFilter = null
    ) {
        ontrack.post("extension/oidc/providers/create", [
                id          : id,
                name        : name,
                description : description,
                issuerId    : issuerId,
                clientId    : clientId,
                clientSecret: clientSecret,
                groupFilter : groupFilter,
        ])
    }

    @DSLMethod("Deletes an OIDC provider")
    void deleteProvider(String id) {
        ontrack.delete("extension/oidc/providers/$id")
    }
}
