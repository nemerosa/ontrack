import {restPromotionLevelImageUri} from "@components/common/Links";
import {useEventForRefresh} from "@components/common/EventsContext";
import ProxyImage from "@components/common/ProxyImage";
import GeneratedIcon from "@components/common/icons/GeneratedIcon";

export const PromotionLevelImage = ({promotionLevel, size = 24, disabled = false}) => {

    const refreshCount = useEventForRefresh("promotionLevel.image")

    return (
        promotionLevel.image ?
            <ProxyImage
                id={`promotion-level-image-${promotionLevel.id}`}
                restUri={`${restPromotionLevelImageUri(promotionLevel)}?key=${refreshCount}`}
                alt={promotionLevel.name}
                width={size}
                height={size}
                disabled={disabled}
            /> : <GeneratedIcon
                name={promotionLevel.name}
                colorIndex={promotionLevel.id}
                size={size}
                disabled={disabled}
            />
    )
}