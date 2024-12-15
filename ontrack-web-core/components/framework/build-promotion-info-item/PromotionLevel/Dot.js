import React from "react";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import {promotionLevelUri} from "@components/common/Links";
import Link from "next/link";
import {Popover, Space} from "antd";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";
import {isAuthorized} from "@components/common/authorizations";
import BuildPromoteAction from "@components/builds/BuildPromoteAction";

export default function PromotionLevelBuildPromotionInfoItemDor({item, build, promotionLevel, onChange}) {
    return (
        <Popover
            title={<PromotionLevelLink promotionLevel={item}/>}
            content={
                <Space direction="vertical">
                    {item.description}
                    {
                        isAuthorized(build, 'build', 'promote') &&
                        <BuildPromoteAction
                            build={build}
                            promotionLevel={item}
                            onPromotion={onChange}
                        />
                    }
                </Space>
            }
        >
            <Link href={promotionLevelUri(item)}>
                <PromotionLevelImage promotionLevel={item} disabled={true}/>
            </Link>
        </Popover>
    )
}