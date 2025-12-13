import React, {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Popover, Space, Timeline, Typography} from "antd";
import dayjs from "dayjs";
import AnnotatedDescription from "@components/common/AnnotatedDescription";
import PromotionLevel from "@components/promotionLevels/PromotionLevel";
import BuildPromoteAction from "@components/builds/BuildPromoteAction";
import {isAuthorized} from "@components/common/authorizations";
import PromotionRunDeleteAction from "@components/promotionRuns/PromotionRunDeleteAction";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import GridCell from "@components/grid/GridCell";
import PromotionRunLink from "@components/promotionRuns/PromotionRunLink";
import {FaCog} from "react-icons/fa";
import EntityNotificationsBadge from "@components/extension/notifications/EntityNotificationsBadge";
import {promotionLevelUri, promotionRunUri} from "@components/common/Links";
import Link from "next/link";
import TimestampText from "@components/common/TimestampText";

/**
 * Listing the promotions and only the promotions of a build.
 */
export default function BuildContentPromotions({build}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [promotionRunItems, setPromotionRunItems] = useState([])

    const [reloadCount, setReloadCount] = useState(0)

    const reload = () => {
        setReloadCount(reloadCount + 1)
    }

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query BuildPromotions($buildId: Int!) {
                        build(id: $buildId) {
                            authorizations {
                                name
                                action
                                authorized
                            }
                            branch {
                                promotionLevels {
                                    id
                                    name
                                    image
                                    description
                                    annotatedDescription
                                }
                            }
                            promotionRuns {
                                id
                                creation {
                                    time
                                    user
                                }
                                authorizations {
                                    name
                                    action
                                    authorized
                                }
                                description
                                annotatedDescription
                                promotionLevel {
                                    id
                                }
                            }
                        }
                    }
                `, {buildId: Number(build.id)}
            ).then(data => {
                // Authorizations
                build.authorizations = data.build.authorizations
                // Gets all the promotion levels in their natural order
                const promotionLevels = data.build.branch.promotionLevels
                // Gets all the promotion runs
                const runs = data.build.promotionRuns
                // For each promotion levels, associate the list of corresponding runs
                promotionLevels.forEach(promotionLevel => {
                    promotionLevel.runs = runs.filter(run => run.promotionLevel.id === promotionLevel.id)
                        .sort((a, b) => a.creation.time.localeCompare(b.creation.time))
                })

                // Converting the list of promotion levels and their runs into a timeline
                const items = []
                promotionLevels.forEach(promotionLevel => {
                    const runs = promotionLevel.runs
                    if (runs && runs.length > 0) {
                        // This promotion has at least 1 run
                        runs.forEach(run => {
                            items.push({
                                label: <Space className={`promotion-run-pl-${run.promotionLevel.id}`}>
                                    {/* Information about the promotion */}
                                    <Popover content={
                                        <Space direction="vertical">
                                            <Typography.Text>Promoted by {run.creation.user}</Typography.Text>
                                            <TimestampText value={run.creation.time}/>
                                            <AnnotatedDescription entity={run}/>
                                        </Space>
                                    }>
                                        {dayjs(run.creation.time).format("YYYY MMM DD, HH:mm")}
                                    </Popover>
                                    {/* Repeating the promotion */}
                                    {
                                        isAuthorized(build, 'build', 'promote') ?
                                            <BuildPromoteAction
                                                build={build}
                                                promotionLevel={promotionLevel}
                                                tooltip={`Promotes the build again to ${promotionLevel.name}`}
                                                onPromotion={reload}
                                            /> : undefined
                                    }
                                    {/* Link to the promotion run */}
                                    {
                                        run &&
                                        <PromotionRunLink
                                            promotionRun={run}
                                            text={<FaCog/>}
                                        />
                                    }
                                    {/* Deleting the promotion */}
                                    {
                                        isAuthorized(run, 'promotion_run', 'delete') ?
                                            <PromotionRunDeleteAction
                                                promotionRun={run}
                                                onDeletion={reload}
                                            /> : undefined
                                    }
                                </Space>,
                                children: <Space>
                                    <Popover title={promotionLevel.name}
                                             content={<AnnotatedDescription entity={promotionLevel}/>}>
                                        <Link href={promotionLevelUri(promotionLevel)}>
                                            <Typography.Text>{promotionLevel.name}</Typography.Text>
                                        </Link>
                                    </Popover>
                                    <EntityNotificationsBadge
                                        entityType="PROMOTION_RUN"
                                        entityId={run.id}
                                        href={promotionRunUri(run)}
                                    />
                                </Space>,
                                dot: <PromotionLevel
                                    promotionLevel={promotionLevel}
                                    size={16}
                                    displayTooltip={false}
                                />
                            })
                        })
                    } else {
                        // This promotion has no run
                        items.push({
                            label: isAuthorized(build, 'build', 'promote') ?
                                <BuildPromoteAction
                                    build={build}
                                    promotionLevel={promotionLevel}
                                    onPromotion={reload}
                                /> : undefined,
                            children: <Popover title={promotionLevel.name}
                                               content={<AnnotatedDescription entity={promotionLevel}/>}>
                                <Link href={promotionLevelUri(promotionLevel)}>
                                    <Typography.Text type="secondary">{promotionLevel.name}</Typography.Text>
                                </Link>
                            </Popover>,
                            dot: <PromotionLevel
                                promotionLevel={promotionLevel}
                                size={16}
                                displayTooltip={false}
                            />
                        })
                    }
                })
                setPromotionRunItems(items)

            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, build, reloadCount]);

    return (
        <>
            <GridCell id="promotions" title="Promotions" loading={loading} padding={true}>
                <Timeline
                    mode="right"
                    reverse={true}
                    items={promotionRunItems}
                />
            </GridCell>
        </>
    )
}