package net.nemerosa.ontrack.dsl.v4.properties

import net.nemerosa.ontrack.dsl.v4.Ontrack
import net.nemerosa.ontrack.dsl.v4.PromotionLevel
import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod
import net.nemerosa.ontrack.dsl.v4.doc.DSLProperties

@DSL
@DSLProperties
class PromotionLevelProperties extends ProjectEntityProperties {

    private final PromotionLevel promotionLevel

    PromotionLevelProperties(Ontrack ontrack, PromotionLevel promotionLevel) {
        super(ontrack, promotionLevel)
        this.promotionLevel = promotionLevel
    }

    /**
     * Promotion dependencies
     */

    @DSLMethod("Sets the validation stamps participating into the auto promotion.")
    void setPromotionDependencies(List<String> promotions) {
        property('net.nemerosa.ontrack.extension.general.PromotionDependenciesPropertyType', [
                dependencies: promotions,
        ])
    }

    @DSLMethod("Gets the validation stamps participating into the auto promotion. The returned list can be null if the property is not defined.")
    List<String> getPromotionDependencies() {
        def value = property('net.nemerosa.ontrack.extension.general.PromotionDependenciesPropertyType', false)
        return value ? value.dependencies as List<String> : null
    }

    /**
     * Auto promotion
     */

    @DSLMethod("Sets the validation stamps participating into the auto promotion.")
    def autoPromotion(String... validationStamps) {
        autoPromotion(validationStamps as List)
    }

    @DSLMethod(id="auto-promotion-patterns", value = "Sets the validation stamps or promotion levels participating into the auto promotion, and sets the include/exclude settings.", count = 4)
    def autoPromotion(Collection<String> validationStamps = [], String include = '', String exclude = '', Collection<String> promotionLevels = []) {
        property(
                'net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType',
                [
                        validationStamps: validationStamps.collect {
                            String vsName -> ontrack.validationStamp(promotionLevel.project, promotionLevel.branch, vsName).id
                        },
                        include         : include,
                        exclude         : exclude,
                        promotionLevels : promotionLevels.collect {
                            String pl -> ontrack.promotionLevel(promotionLevel.project, promotionLevel.branch, pl).id
                        },
                ]
        )
    }

    @DSLMethod("Checks if the promotion level is set in auto promotion.")
    boolean getAutoPromotion() {
        property('net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType')
    }

}
