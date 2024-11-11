import {promotionLevelUri, promotionRunUri} from "@components/common/Links";
import {Space, Typography} from "antd";
import Link from "next/link";
import React from "react";
import EntityNotificationsBadge from "@components/extension/notifications/EntityNotificationsBadge";

export default function PromotionRunBuildPromotionInfoItemName({item, build, promotionLevel, onChange}) {
    return (
        <Link href={promotionLevelUri(promotionLevel)}>
            <Space>
                <Typography.Text type="secondary">{promotionLevel.name}</Typography.Text>
                <EntityNotificationsBadge
                    entityType="PROMOTION_RUN"
                    entityId={item.id}
                    href={promotionRunUri(item)}
                />
            </Space>
        </Link>
    )
}