package net.nemerosa.ontrack.extension.casc.secrets

import net.nemerosa.ontrack.model.structure.NameDescription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.casc.secrets",
    name = ["type"],
    havingValue = "env",
    matchIfMissing = true,
)
class EnvCascSecretService(
    private val envAccessor: (String) -> String,
) : CascSecretService {

    @Autowired
    constructor() : this({ name -> System.getenv(name) ?: "" })

    override fun getValue(ref: String): String {
        val name = "SECRET_" + NameDescription.escapeName(ref.uppercase())
            .replace("-", "_")
            .replace(".", "_")
        return envAccessor(name)
    }
}