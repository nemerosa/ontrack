import {Space} from "antd";
import PromotionRun from "@components/promotionRuns/PromotionRun";

export default function PromotionRuns({promotionRuns}) {
    return (
        <>
            {
                promotionRuns && <Space size={8}>
                    {
                        promotionRuns.map(promotionRun =>
                            <PromotionRun
                                key={promotionRun.id}
                                promotionRun={promotionRun}
                                size={16}
                            />
                        )
                    }
                </Space>
            }
        </>
    )
}