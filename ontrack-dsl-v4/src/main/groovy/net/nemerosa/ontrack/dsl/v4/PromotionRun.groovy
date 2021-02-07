package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod
import net.nemerosa.ontrack.dsl.v4.properties.PromotionRunProperties

@DSL
class PromotionRun extends AbstractProjectResource {

    PromotionRun(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    @Override
    PromotionRunProperties getConfig() {
        new PromotionRunProperties(ontrack, this)
    }

    @DSLMethod("Gets the associated promotion level (JSON)")
    def getPromotionLevel() {
        node.promotionLevel
    }
}
