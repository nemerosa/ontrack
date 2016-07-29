package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL
class PredefinedValidationStamp extends AbstractResource {

    PredefinedValidationStamp(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    int getId() {
        node['id'] as int
    }

    String getName() {
        node['name']
    }

    String getDescription() {
        node['description']
    }

    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    @DSLMethod("Sets the image for this validation stamp (must be a PNG file). See <<dsl-usecases-images>>.")
    def image(Object o) {
        image(o, 'image/png')
    }

    def image(Object o, String contentType) {
        ontrack.upload(link('image'), 'file', o, contentType)
    }

    @DSLMethod("Downloads the image for the validation stamp. See <<dsl-usecases-images>>.")
    Document getImage() {
        ontrack.download(link('image'))
    }
}
