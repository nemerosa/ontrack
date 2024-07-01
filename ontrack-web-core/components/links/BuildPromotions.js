import PromotionRun from "@components/promotionRuns/PromotionRun";
import {Space} from "antd";

export default function BuildPromotions({build, lastOnly}) {
    return (
        <>
            {
                build && build.promotionRuns &&
                <>
                    {
                        lastOnly && <PromotionRun
                            promotionRun={build.promotionRuns[build.promotionRuns.length - 1]}
                            size={16}
                        />
                    }
                    {
                        !lastOnly &&
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
                    }
                </>
            }
        </>
    )
}