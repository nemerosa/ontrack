package net.nemerosa.ontrack.extension.dm.data

import net.nemerosa.ontrack.extension.dm.model.EndToEndPromotionRecord

/**
 * Gets raw data about end-to-end promotions, following any build link in Ontrack.
 */
interface EndToEndPromotionsHelper {

    fun forEachEndToEndPromotionRecord(
        filter: EndToEndPromotionFilter = EndToEndPromotionFilter(),
        code: (record: EndToEndPromotionRecord) -> Unit,
    )

}