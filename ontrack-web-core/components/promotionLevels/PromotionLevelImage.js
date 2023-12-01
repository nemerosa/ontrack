import {legacyPromotionLevelImageUri} from "@components/common/Links";
import LegacyImage from "@components/common/LegacyImage";

export const PromotionLevelImage = ({promotionLevel, size = 16}) => {
    return (
        promotionLevel.image ?
            <LegacyImage href={legacyPromotionLevelImageUri(promotionLevel)}
                         alt={promotionLevel.name}
                         width={size}
                         height={size}
            /> : undefined
    )
}