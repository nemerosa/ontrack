import PromotionLevel from "@components/promotionLevels/PromotionLevel";
import Timestamp from "@components/common/Timestamp";

export default function PromotionRun({
                                         promotionRun, size = 16,
                                         displayDetails = true,
                                         displayPromotionLevelName = false
                                     }) {
    return (
        <>
            <PromotionLevel
                promotionLevel={promotionRun.promotionLevel}
                size={size}
                displayTooltip={displayDetails}
                displayText={displayPromotionLevelName}
                details={
                    displayDetails ?
                        <Timestamp prefix="Promoted on " value={promotionRun.creation.time}/> : undefined
                }
            />
        </>
    )
}