import {Space} from "antd";
import PromotionRunLink from "@components/promotionRuns/PromotionRunLink";
import BuildLink from "@components/builds/BuildLink";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";

/**
 * This displays a link to a promotion run with a default text
 * being "<run> Build <build> promoted to <promotion>".
 */
export default function DefaultPromotionRunLink({promotionRun}) {
    return (
        <>
            <Space>
                Build
                <BuildLink build={promotionRun.build}/>
                <PromotionRunLink
                    promotionRun={promotionRun}
                    text="promoted"
                />
                to
                <PromotionLevelLink promotionLevel={promotionRun.promotionLevel}/>

            </Space>
        </>
    )
}