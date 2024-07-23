import PromotionLevel from "@components/promotionLevels/PromotionLevel";
import Timestamp from "@components/common/Timestamp";
import {promotionRunUri} from "@components/common/Links";
import Link from "next/link";

export default function PromotionRun({
                                         promotionRun, size = 16,
                                         displayDetails = true,
                                         displayPromotionLevelName = false,
                                         displayPromotionLevelDescription = false,
                                     }) {
    return (
        <>
            {
                promotionRun &&
                <Link href={promotionRunUri(promotionRun)}>
                    <PromotionLevel
                        promotionLevel={promotionRun.promotionLevel}
                        size={size}
                        displayTooltip={displayDetails}
                        displayText={displayPromotionLevelName}
                        displayDescription={displayPromotionLevelDescription}
                        details={
                            displayDetails ?
                                <Timestamp prefix="Promoted on " value={promotionRun.creation.time}/> : undefined
                        }
                    />
                </Link>
            }
        </>
    )
}