import {Popover, Space, Typography} from "antd";
import PromotionLevelLink from "@components/promotionLevels/PromotionLevelLink";
import TimestampText from "@components/common/TimestampText";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import EntityNotificationsBadge from "@components/extension/notifications/EntityNotificationsBadge";
import {promotionRunUri} from "@components/common/Links";
import {isAuthorized} from "@components/common/authorizations";
import BuildPromoteAction from "@components/builds/BuildPromoteAction";
import PromotionRunDeleteAction from "@components/promotionRuns/PromotionRunDeleteAction";
import Link from "next/link";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import React from "react";

/**
 * Representation of a promotion run to place in a list of steps.
 *
 * @param run Promotion run
 * @param onChange Function to call when the promotion run triggers an action (delete or promotion)
 */
export default function PromotionRunStep({run, onChange}) {
    return (
        <>
            <Popover
                title={<PromotionLevelLink promotionLevel={run.promotionLevel}/>}
                content={
                    <Space direction="vertical">
                        <Typography.Text>Promoted by {run.creation.user}</Typography.Text>
                        <TimestampText value={run.creation.time}/>
                        <AnnotatedDescription entity={run}/>
                        <EntityNotificationsBadge
                            entityType="PROMOTION_RUN"
                            entityId={run.id}
                            href={promotionRunUri(run)}
                            showText={true}
                        />
                        <Space>
                            {/* Repromoting */}
                            {
                                isAuthorized(run, 'build', 'promote') ?
                                    <BuildPromoteAction
                                        build={run.build}
                                        promotionLevel={run.promotionLevel}
                                        onPromotion={onChange}
                                    /> : undefined

                            }
                            {/* Deleting the promotion */}
                            {
                                isAuthorized(run, 'promotion_run', 'delete') ?
                                    <PromotionRunDeleteAction
                                        promotionRun={run}
                                        onDeletion={onChange}
                                    /> : undefined
                            }
                        </Space>
                    </Space>
                }
            >
                <Link href={promotionRunUri(run)}>
                    <EntityNotificationsBadge
                        entityType="PROMOTION_RUN"
                        entityId={run.id}
                        href={promotionRunUri(run)}
                    >
                        <PromotionLevelImage promotionLevel={run.promotionLevel}/>
                    </EntityNotificationsBadge>
                </Link>
            </Popover>
        </>
    )
}