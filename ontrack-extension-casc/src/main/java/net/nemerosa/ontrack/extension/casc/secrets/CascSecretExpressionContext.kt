package net.nemerosa.ontrack.extension.casc.secrets

import net.nemerosa.ontrack.extension.casc.expressions.CascExpressionContext
import org.springframework.stereotype.Component

@Component
class CascSecretExpressionContext(
    private val cascSecretService: CascSecretService,
) : CascExpressionContext {

    override val name: String = "secret"

    override fun evaluate(value: String): String =
        cascSecretService.getValue(value)

}