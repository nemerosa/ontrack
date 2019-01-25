package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.properties.PromotionRunProperties

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

    @DSLMethod("Gets the associated promotion level name")
    def getPromotionLevelName() {
        node.promotionLevel.name.asText()
    }
}
