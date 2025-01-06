import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Progress} from "antd";
import LoadingInline from "@components/common/LoadingInline";
import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotPipelineDeploymentStatusProgress({pipeline, link = true, reloadState, size}) {

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
    }, [client, pipeline.id, reloadState])

    const progressComponent = () => <Progress
        strokeColor={progress?.overridden ? "orange" : undefined}
        data-testid={`pipeline-progress-${pipeline.id}`}
        type="circle"
        percent={progress?.percentage}
        size={size === "small" ? 16 : 32}
        title={
            progress?.overridden ? "Some rules have been overridden" : "All admission rules have been validated"
        }
    />

    return (
        <>
            <LoadingInline loading={loading}>
                {
                    link &&
                    <Link href={slotPipelineUri(pipeline.id)} title="Deployment details">
                        {progressComponent()}
                    </Link>
                }
                {
                    !link && progressComponent()
                }
            </LoadingInline>
        </>
    )
}