import {Popover, Typography} from "antd";
import {PromotionLevelImage} from "@components/common/Links";
import PromotionLevel from "@components/promotionLevels/PromotionLevel";
import Timestamp from "@components/common/Timestamp";

export default function PromotionRun({promotionRun, size = 16, displayDetails = true}) {
    return (
        <>
            <PromotionLevel
                promotionLevel={promotionRun.promotionLevel}
                size={size}
                displayTooltip={displayDetails}
                details={
                    displayDetails ?
                        <Timestamp prefix="Promoted on " value={promotionRun.creation.time}/> : undefined
                }
            />
        </>
    )
}