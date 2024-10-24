import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Progress} from "antd";
import LoadingInline from "@components/common/LoadingInline";
import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotPipelineDeploymentStatusProgress({pipeline, link = true}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [progress, setProgress] = useState()

    useEffect(() => {
        if (client && pipeline.id) {
            setLoading(true)
            client.request(
                gql`
                    query SlotPipelineDeploymentStatusProgress($id: String!) {
                        slotPipelineById(id: $id) {
                            deploymentStatus {
                                progress {
                                    ok
                                    overridden
                                    total
                                    percentage
                                }
                            }
                        }
                    }
                `,
                {id: pipeline.id}
            ).then(data => {
                setProgress(data.slotPipelineById.deploymentStatus.progress)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, pipeline.id])

    return (
        <>
            {/* TODO Show if overridden */}
            <LoadingInline loading={loading}>
                {
                    link &&
                    <Link href={slotPipelineUri(pipeline.id)} title="Pipeline details">
                        <Progress type="circle" percent={progress?.percentage} size={32}/>
                    </Link>
                }
                {
                    !link &&
                    <Progress type="circle" percent={progress?.percentage} size={32}/>
                }
            </LoadingInline>
        </>
    )
}