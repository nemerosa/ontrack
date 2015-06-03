package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PromotionLevel

class PromotionLevelProperties extends ProjectEntityProperties {

    private final PromotionLevel promotionLevel

    PromotionLevelProperties(Ontrack ontrack, PromotionLevel promotionLevel) {
        super(ontrack, promotionLevel)
        this.promotionLevel = promotionLevel
    }

    /**
     * Auto promotion
     */

    def autoPromotion(String... validationStamps) {
        autoPromotion(validationStamps as List)
    }

    def autoPromotion(Collection<String> validationStamps) {
        property(
                'net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType',
                [
                        validationStamps: validationStamps.collect {
                            String vsName -> ontrack.validationStamp(promotionLevel.project, promotionLevel.branch, vsName).id
                        }
                ]
        )
    }

    boolean getAutoPromotion() {
        property('net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType')
    }

}
