import React from "react";
import {Popover, Space, Typography} from "antd";
import TimestampText from "@components/common/TimestampText";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import {isAuthorized} from "@components/common/authorizations";
import PromotionRunLink from "@components/promotionRuns/PromotionRunLink";
import {FaCog} from "react-icons/fa";
import PromotionRunDeleteAction from "@components/promotionRuns/PromotionRunDeleteAction";
import {promotionRunUri} from "@components/common/Links";

export default function PromotionRunBuildPromotionInfoItemActions({item, build, promotionLevel, onChange}) {
    return (
        <>
            <Space>
                <Popover content={
                    <Space direction="vertical">
                        <Typography.Text>Promoted by {item.creation.user}</Typography.Text>
                        <TimestampText value={item.creation.time}/>
                        <AnnotatedDescription entity={item}/>
                    </Space>
                }>
                    <Typography.Link href={promotionRunUri(item)}>
                        <TimestampText value={item.creation.time}/>
                    </Typography.Link>
                </Popover>
                {/* Link to the promotion run */}
                {
                    <PromotionRunLink
                        promotionRun={item}
                        text={<FaCog/>}
                    />
                }
                {/* Deleting the promotion */}
                {
                    isAuthorized(item, 'promotion_run', 'delete') ?
                        <PromotionRunDeleteAction
                            promotionRun={item}
                            onDeletion={onChange}
                        /> : undefined
                }
            </Space>
        </>
    )
}