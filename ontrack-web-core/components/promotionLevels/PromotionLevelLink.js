import {Space} from "antd";
import {promotionLevelUri} from "@components/common/Links";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import Link from "next/link";

export default function PromotionLevelLink({promotionLevel, text, size}) {
    return (
        <>
            <Link href={promotionLevelUri(promotionLevel)}>
                <Space>
                    <PromotionLevelImage size={size} promotionLevel={promotionLevel}/>
                    {text ? text : promotionLevel.name}
                </Space>
            </Link>
        </>
    )
}