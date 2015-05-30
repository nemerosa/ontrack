package net.nemerosa.ontrack.dsl

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

    def image(Object o) {
        image(o, 'image/png')
    }

    def image(Object o, String contentType) {
        ontrack.upload(link('image'), 'file', o, contentType)
    }

    Document getImage() {
        ontrack.download(link('image'))
    }
}
