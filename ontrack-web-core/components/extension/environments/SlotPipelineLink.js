import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import LoadingInline from "@components/common/LoadingInline";
import {gql} from "graphql-request";

export default function SlotPipelineLink({pipelineId}) {

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
            <Link href={slotPipelineUri(pipelineId)}>
                <LoadingInline loading={loading}>
                    Pipeline {pipeline.slot.environment.name}/{pipeline.slot.project.name}{pipeline.slot.qualifier && `/${pipeline.slot.qualifier}`}#{pipeline.number}
                </LoadingInline>
            </Link>
        </>
    )
}