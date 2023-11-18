import PromotionRun from "@components/promotionRuns/PromotionRun";
import {Space} from "antd";

export default function BuildPromotions({build}) {
    return (
        <>
            <Space size={8}>
                {
                    build.promotionRuns.map(promotionRun =>
                        <PromotionRun
                            key={promotionRun.id}
                            promotionRun={promotionRun}
                            size={16}
                        />
                    )
                }
            </Space>
        </>
    )
}