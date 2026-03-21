import PromotionRun from "@components/promotionRuns/PromotionRun";
import {Space} from "antd";
import {promotionRunUri} from "@components/common/Links";
import EntityNotificationsBadge from "@components/extension/notifications/EntityNotificationsBadge";

export default function BuildPromotions({build, lastOnly}) {
    const lastPromotionRun = build.promotionRuns[build.promotionRuns.length - 1];
    return (
        <>
            {
                build && build.promotionRuns &&
                <>
                    {
                        lastOnly && lastPromotionRun && <EntityNotificationsBadge
                            key={lastPromotionRun.id}
                            entityType="PROMOTION_RUN"
                            entityId={lastPromotionRun.id}
                            href={promotionRunUri(lastPromotionRun)}
                        >
                            <PromotionRun
                                promotionRun={lastPromotionRun}
                                size={16}
                            />
                        </EntityNotificationsBadge>
                    }
                    {
                        !lastOnly &&
                        <Space size={8}>
                            {
                                build.promotionRuns.map(promotionRun =>
                                    <EntityNotificationsBadge
                                        key={promotionRun.id}
                                        entityType="PROMOTION_RUN"
                                        entityId={promotionRun.id}
                                        href={promotionRunUri(promotionRun)}
                                    >
                                        <PromotionRun
                                            promotionRun={promotionRun}
                                            size={16}
                                        />
                                    </EntityNotificationsBadge>
                                )
                            }
                        </Space>
                    }
                </>
            }
        </>
    )
}