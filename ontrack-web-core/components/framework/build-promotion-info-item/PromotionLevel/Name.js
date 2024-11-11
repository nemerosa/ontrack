import {promotionLevelUri} from "@components/common/Links";
import {Space, Typography} from "antd";
import Link from "next/link";
import React from "react";

export default function PromotionLevelBuildPromotionInfoItemName({item, build, promotionLevel, onChange}) {
    return (
        <Link href={promotionLevelUri(item)}>
            <Space>
                <Typography.Text type="secondary">{item.name}</Typography.Text>
            </Space>
        </Link>
    )
}