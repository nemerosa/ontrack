package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties
import net.nemerosa.ontrack.dsl.properties.PromotionLevelProperties

@DSL
class PromotionLevel extends AbstractProjectResource {

    PromotionLevel(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Name of the associated project.")
    String getProject() {
        node?.branch?.project?.name
    }

    @DSLMethod("Name of the associated branch.")
    String getBranch() {
        node?.branch?.name
    }

    @DSLMethod("Configuration of the promotion level with a closure.")
    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    @DSLMethod("Access to the promotion level properties")
    PromotionLevelProperties getConfig() {
        new PromotionLevelProperties(ontrack, this)
    }

    @DSLMethod("Sets the promotion level image (see <<dsl-usecases-images>>)")
    def image(Object o) {
        image(o, 'image/png')
    }

    @DSLMethod("Sets the promotion level image (see <<dsl-usecases-images>>)")
    def image(Object o, String contentType) {
        ontrack.upload(link('image'), 'file', o, contentType)
    }

    @DSLMethod("Gets the promotion level image (see <<dsl-usecases-images>>)")
    Document getImage() {
        ontrack.download(link('image'))
    }

    /**
     * Auto promotion decoration (for a promotion level)
     */
    @DSLMethod("Checks if this promotion level is set in <<promotion-levels-auto-promotion,auto decoration mode>>.")
    Boolean getAutoPromotionPropertyDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.general.AutoPromotionPropertyDecorator') as Boolean
    }
}
