package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.PromotionRunProperties

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
}
