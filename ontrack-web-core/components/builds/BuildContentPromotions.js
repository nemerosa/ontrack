import PageSection from "@components/common/PageSection";
import React, {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {Popover, Space, Timeline, Typography} from "antd";
import dayjs from "dayjs";
import PromotionRun from "@components/promotionRuns/PromotionRun";

export default function BuildContentPromotions({build}) {

    const [loading, setLoading] = useState(true)
    const [promotionRunItems, setPromotionRunItems] = useState([])

    useEffect(() => {
        setLoading(true)
        graphQLCall(
            gql`
                query BuildPromotions($buildId: Int!) {
                    build(id: $buildId) {
                        promotionRuns(lastPerLevel: true) {
                            id
                            creation {
                                time
                                user
                            }
                            annotatedDescription
                            promotionLevel {
                                id
                                name
                                image
                            }
                        }
                    }
                }
            `, {buildId: build.id}
        ).then(data => {
            setPromotionRunItems(data.build.promotionRuns.map(run => {
                return {
                    label: <Popover title={
                        <Space direction="vertical">
                            <Typography.Text>Promoted by {run.creation.user}</Typography.Text>
                            {dayjs(run.creation.time).format("YYYY MMM DD, HH:mm:ss")}
                        </Space>
                    }>
                        {dayjs(run.creation.time).format("YYYY MMM DD, HH:mm")}
                    </Popover>,
                    children: run.promotionLevel.name,
                    dot: <PromotionRun
                        promotionRun={run}
                        size={16}
                        displayDetails={false}
                    />
                }
            }))
        }).finally(() => {
            setLoading(false)
        })
    }, [build]);

    return (
        <>
            <PageSection title="Promotions" loading={loading}>
                <Timeline
                    mode="right"
                    items={promotionRunItems}
                />
            </PageSection>
        </>
    )
}