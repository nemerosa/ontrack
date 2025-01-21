import {restPredefinedPromotionLevelImageUri} from "@components/common/Links";
import ProxyImage from "@components/common/ProxyImage";
import GeneratedIcon from "@components/common/icons/GeneratedIcon";

export default function PredefinedPromotionLevelImage({predefinedPromotionLevel, title, size = 24}) {
    return (
        <>
            {
                predefinedPromotionLevel.isImage ?
                    <ProxyImage
                        restUri={restPredefinedPromotionLevelImageUri(predefinedPromotionLevel)}
                        alt={title}
                        width={size}
                        height={size}
                    /> : <GeneratedIcon
                        name={predefinedPromotionLevel.name}
                        colorIndex={predefinedPromotionLevel.id}
                        size={size}
                    />
            }
        </>
    )
}