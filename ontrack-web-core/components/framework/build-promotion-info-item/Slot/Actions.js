import {Space} from "antd";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import BuildLink from "@components/builds/BuildLink";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import Link from "next/link";
import LoadingInline from "@components/common/LoadingInline";

export default function SlotBuildPromotionInfoItemActions({item, build, onChange}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [pipeline, setPipeline] = useState()

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query SlotCurrentPipeline($slotId: String!) {
                        slotById(id: $slotId) {
                            currentPipeline {
                                id
                                status
                                build {
                                    id
                                    name
                                    releaseProperty {
                                        value
                                    }
                                }
                            }
                        }
                    }
                `,
                {
                    slotId: item.id
                }
            ).then(data => {
                setPipeline(data.slotById.currentPipeline)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, item.id])

    return (
        <>
            <Space>
                <SlotPipelineCreateButton
                    slot={item}
                    build={build}
                    onStart={onChange}
                    size="small"
                />
                {/* Current build & status */}
                <LoadingInline loading={loading}>
                    {
                        pipeline &&
                        <Space>
                            <BuildLink build={pipeline.build} displayTooltip={true}
                                       tooltipText="Build being currently in the pipeline for this environment"/>
                            <Link href={slotPipelineUri(pipeline.id)} title="Pipeline details">
                                <SlotPipelineStatus pipeline={pipeline} showText={false}/>
                            </Link>
                        </Space>
                    }
                </LoadingInline>
            </Space>
        </>
    )
}