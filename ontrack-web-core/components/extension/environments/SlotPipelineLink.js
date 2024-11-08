import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import LoadingInline from "@components/common/LoadingInline";
import {gql} from "graphql-request";
import {Space} from "antd";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";

export default function SlotPipelineLink({pipelineId, status}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [pipeline, setPipeline] = useState()
    useEffect(() => {
        if (client && pipelineId) {
            setLoading(true)
            client.request(
                gql`
                    query PipelineLink($id: String!) {
                        slotPipelineById(id: $id) {
                            id
                            status
                            number
                            slot {
                                project {
                                    name
                                }
                                qualifier
                                environment {
                                    name
                                }
                            }
                        }
                    }
                `,
                {id: pipelineId}
            ).then(data => {
                setPipeline(data.slotPipelineById)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, pipelineId])

    return (
        <>
            <LoadingInline loading={loading}>
                {
                    pipeline &&
                    <Space>
                        <Link href={slotPipelineUri(pipelineId)}>
                            Pipeline {pipeline.slot.environment.name}/{pipeline.slot.project.name}{pipeline.slot.qualifier && `/${pipeline.slot.qualifier}`}#{pipeline.number}
                        </Link>
                        {status && <SlotPipelineStatus pipeline={pipeline}/>}
                    </Space>
                }
            </LoadingInline>
        </>
    )
}