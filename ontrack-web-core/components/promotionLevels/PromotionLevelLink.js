import {Space} from "antd";
import LegacyLink from "@components/common/LegacyLink";
import {legacyPromotionLevelUri} from "@components/common/Links";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";

export default function PromotionLevelLink({promotionLevel, text}) {
    return (
        <>
            <LegacyLink href={legacyPromotionLevelUri(promotionLevel)}>
                <Space>
                    <PromotionLevelImage promotionLevel={promotionLevel}/>
                    {text ? text : promotionLevel.name}
                </Space>
            </LegacyLink>
        </>
    )
}