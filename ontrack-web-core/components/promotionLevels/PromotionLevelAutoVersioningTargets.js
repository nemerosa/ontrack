import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Skeleton} from "antd";
import {gql} from "graphql-request";
import {gqlAutoVersioningTrailContent} from "@components/extension/auto-versioning/AutoVersioningGraphQLFragments";
import AutoVersioningTrail from "@components/extension/auto-versioning/AutoVersioningTrail";

export default function PromotionLevelAutoVersioningTargets({promotionLevel}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [trail, setTrail] = useState()

    useEffect(() => {
        if (client && promotionLevel) {
            setLoading(true)
            client.request(
                gql`
                    query GetPromotionLevelAVTargets($id: Int!) {
                        promotionLevel(id: $id) {
                            autoVersioningTrail {
                                ...AutoVersioningTrailContent
                            }
                        }
                    }
                    ${gqlAutoVersioningTrailContent}
                `,
                {
                    id: Number(promotionLevel.id),
                }
            ).then(data => {
                setTrail(data.promotionLevel.autoVersioningTrail)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, promotionLevel])

    return (
        <>
            <Skeleton loading={loading} active>
                {
                    trail &&
                    <AutoVersioningTrail trail={trail} displayAudit={false}/>
                }
            </Skeleton>
        </>
    )
}