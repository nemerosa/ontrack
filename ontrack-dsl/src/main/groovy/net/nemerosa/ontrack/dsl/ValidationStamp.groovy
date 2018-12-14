package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.properties.ValidationStampProperties

@DSL
class ValidationStamp extends AbstractProjectResource {

    ValidationStamp(Ontrack ontrack, Object node) {
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

    @DSLMethod("Access to the validation stamp properties")
    ValidationStampProperties getConfig() {
        new ValidationStampProperties(ontrack, this)
    }

    @DSLMethod("Sets the validation stamp image (see <<dsl-usecases-images>>)")
    def image(Object o) {
        image(o, 'image/png')
    }

    @DSLMethod("Sets the validation stamp image (see <<dsl-usecases-images>>)")
    def image(Object o, String contentType) {
        ontrack.upload(link('image'), 'file', o, contentType)
    }

    @DSLMethod("Gets the validation stamp image (see <<dsl-usecases-images>>)")
    Document getImage() {
        ontrack.download(link('image'))
    }

    /**
     * Validation stamp weather decoration: <code>weather</code> and <code>text</code>
     */
    @DSLMethod("Gets the validation stamp weather decoration.")
    def getValidationStampWeatherDecoration() {
        getDecoration('net.nemerosa.ontrack.extension.general.ValidationStampWeatherDecorationExtension')
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

    @DSLMethod("Sets the data type for this validation stamp to 'text'.")
    def setTextDataType() {
        setDataType(
                "net.nemerosa.ontrack.extension.general.validation.TextValidationDataType",
                [:]
        )
    }

    @DSLMethod("Sets the data type for this validation stamp to 'CHML' (number of critical / high / medium / low issues).")
    def setCHMLDataType(
            String warningLevel,
            Integer warningValue,
            String failedLevel,
            Integer failedValue
    ) {
        setDataType(
                "net.nemerosa.ontrack.extension.general.validation.CHMLValidationDataType",
                [
                        warningLevel: warningLevel,
                        warningValue: warningValue,
                        failedLevel : failedLevel,
                        failedValue : failedValue
                ]
        )
    }

    @DSLMethod(value = "Sets the data type for this validation stamp to 'Number'.", count = 3)
    def setNumberDataType(
            Integer warningThreshold = null,
            Integer failureThreshold = null,
            boolean okIfGreater = true
    ) {
        setDataType(
                "net.nemerosa.ontrack.extension.general.validation.ThresholdNumberValidationDataType",
                [
                        warningThreshold: warningThreshold,
                        failureThreshold: failureThreshold,
                        okIfGreater     : okIfGreater,
                ]
        )
    }

    @DSLMethod(value = "Sets the data type for this validation stamp to 'Percentage'.", count = 3)
    def setPercentageDataType(
            Integer warningThreshold = null,
            Integer failureThreshold = null,
            boolean okIfGreater = true
    ) {
        setDataType(
                "net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType",
                [
                        warningThreshold: warningThreshold,
                        failureThreshold: failureThreshold,
                        okIfGreater     : okIfGreater,
                ]
        )
    }

    @DSLMethod(value = "Sets the data type for this validation stamp to 'Fraction'.", count = 3)
    def setFractionDataType(
            Integer warningThreshold = null,
            Integer failureThreshold = null,
            boolean okIfGreater = true
    ) {
        setDataType(
                "net.nemerosa.ontrack.extension.general.validation.FractionValidationDataType",
                [
                        warningThreshold: warningThreshold,
                        failureThreshold: failureThreshold,
                        okIfGreater     : okIfGreater,
                ]
        )
    }
}
