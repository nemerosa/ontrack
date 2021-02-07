package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

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

    @DSLMethod("Sets a data type for the validation stamp")
    def setDataType(String id, Object config) {
        ontrack.put(
                link("update"),
                [
                        name       : name,
                        description: description,
                        dataType   : [
                                id  : id,
                                data: config,
                        ],
                ]
        )
    }

    @DSLMethod("Gets the data type for the validation stamp, map with `id` and `config`, or null if not defined.")
    def getDataType() {
        if (node.dataType) {
            return [
                    id    : node.dataType.descriptor.id,
                    config: node.dataType.config
            ]
        } else {
            return null
        }
    }
}
