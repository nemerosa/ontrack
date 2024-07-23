import {Space} from "antd";
import {promotionLevelUri} from "@components/common/Links";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import Link from "next/link";

export default function PromotionLevelLink({promotionLevel, text}) {
    return (
        <>
            <Link href={promotionLevelUri(promotionLevel)}>
                <Space>
                    <PromotionLevelImage promotionLevel={promotionLevel}/>
                    {text ? text : promotionLevel.name}
                </Space>
            </Link>
        </>
    )
}