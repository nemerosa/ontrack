import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {gqlSlotData, gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import LoadingInline from "@components/common/LoadingInline";
import React, {useEffect, useState} from "react";
import {Space, Timeline} from "antd";
import Link from "next/link";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {FaServer} from "react-icons/fa";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";
import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";
import SlotPipelineStatusActions from "@components/extension/environments/SlotPipelineStatusActions";
import TimestampText from "@components/common/TimestampText";

/**
 * For a given build, displays:
 *
 * * the list of pipelines for this build, with their target, status & commands
 * * the list of slots this build is eligible to.
 */
export default function BuildEnvironments({build}) {

    const {data, loading, error, refetch} = useQuery(
        gql`
            query BuildEnvironments($buildId: Int!) {
                build(id: $buildId) {
                    authorizations {
                        name
                        action
                        authorized
                    }
                    slotPipelines {
                        ...SlotPipelineData
                    }
                    eligibleSlots {
                        ...SlotData
                    }
                }
            }
            ${gqlSlotData}
            ${gqlSlotPipelineData}
        `,
        {variables: {buildId: build.id}}
    )

    const [items, setItems] = useState([])
    useEffect(() => {
        if (data) {
            const items = []

            data.build.slotPipelines.forEach(pipeline => {
                items.push({
                    label: <Space>
                        <TimestampText value={pipeline.start}/>
                        <SlotPipelineStatusActions
                            pipeline={pipeline}
                            linkInfo={false}
                            size="small"
                            onChange={refetch}
                        />
                    </Space>,
                    children: <Space>
                        <FaServer/>
                        <SlotPipelineLink
                            pipelineId={pipeline.id}
                        />
                    </Space>
                })
            })

            data.build.eligibleSlots.forEach(slot => {
                items.push({
                    label: isAuthorized(build, 'slotPipeline', 'create') ?
                        <SlotPipelineCreateButton
                            slot={slot}
                            build={build}
                            size="small"
                            onStart={refetch}
                        /> : undefined,
                    children: <Space>
                        <FaServer/>
                        <Link href={slotUri(slot)}>
                            {slot.environment.name}/{slot.project.name}{slot.qualifier ? `/${slot.qualifier}` : ''}
                        </Link>
                    </Space>,
                })
            })

            setItems(items)
        }
    }, [data])

    return (
        <>
            <LoadingInline loading={loading}>
                <Timeline
                    mode="right"
                    reverse={true}
                    items={items}
                />
            </LoadingInline>
        </>
    )
}