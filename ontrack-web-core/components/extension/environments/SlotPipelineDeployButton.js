import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Button, message, Popconfirm, Space} from "antd";
import {FaInfoCircle, FaPlay} from "react-icons/fa";
import LoadingInline from "@components/common/LoadingInline";
import {gql} from "graphql-request";
import SlotPipelineDeploymentStatusButton from "@components/extension/environments/SlotPipelineDeploymentStatusButton";
import {getUserErrors} from "@components/services/graphql-utils";

export default function SlotPipelineDeployButton({pipeline, onDeploy}) {

    const [messageApi, contextHolder] = message.useMessage()
    const client = useGraphQLClient()

    const [stateReload, setStateReload] = useState(0)
    const [loadingDeployable, setLoadingDeployable] = useState(true)
    const [deploymentStatus, setDeploymentStatus] = useState()

    const [deploying, setDeploying] = useState(false)

    const gqlDeploymentStatus = gql`
        fragment DeploymentStatusData on SlotPipelineDeploymentStatus {
            status
            override
            checks {
                check {
                    status
                    reason
                }
                ruleId
                ruleConfig
                ruleData
                override {
                    timestamp
                    user
                    override
                    overrideMessage
                }
            }
        }
    `

    useEffect(() => {
        if (client && pipeline) {
            setLoadingDeployable(true)
            client.request(
                gql`
                    query PipelineDeployable($id: String!) {
                        slotPipelineById(id: $id) {
                            deploymentStatus {
                                ...DeploymentStatusData
                            }
                        }
                    }
                    ${gqlDeploymentStatus}
                `,
                {id: pipeline.id}
            ).then(data => {
                setDeploymentStatus(data.slotPipelineById?.deploymentStatus)
            }).finally(() => {
                setLoadingDeployable(false)
            })
        }
    }, [client, pipeline, stateReload])

    const deploy = async () => {
        setDeploying(true)
        try {
            const data = await client.request(
                gql`
                    mutation DeployPipeline($id: String!) {
                        startSlotPipelineDeployment(input: {
                            pipelineId: $id,
                        }) {
                            deploymentStatus {
                                ...DeploymentStatusData
                            }
                            errors {
                                message
                            }
                        }
                    }

                    ${gqlDeploymentStatus}
                `,
                {
                    id: pipeline.id,
                }
            )
            // Errors
            const errors = getUserErrors(data.startSlotPipelineDeployment)
            if (errors) {
                messageApi.error(
                    `Error triggering the deployment: ${errors}`
                )
            } else {
                // Status
                const deploymentStatus = data.startSlotPipelineDeployment.deploymentStatus
                if (deploymentStatus) {
                    setDeploymentStatus(deploymentStatus)
                    setStateReload(count => count + 1)
                    if (onDeploy) onDeploy()
                } else {
                    messageApi.error(
                        "Did not receive any deployment status"
                    )
                }
            }
        } finally {
            setDeploying(false)
        }
    }

    return (
        <>
            {contextHolder}
            {
                deploymentStatus &&
                <LoadingInline loading={loadingDeployable} text="">
                    <Space>
                        {
                            pipeline.status === 'ONGOING' && !deploymentStatus.status &&
                            <Button
                                icon={<FaInfoCircle color="green"/>}
                                title="Deploys this pipeline"
                                loading={loadingDeployable}
                            />
                        }
                        {
                            pipeline.status === 'ONGOING' && deploymentStatus.status &&
                            <Popconfirm
                                title="Deploying pipeline"
                                description="Deploying this pipeline may trigger an event to perform an actual deployment. Do you want to continue?"
                                onConfirm={deploy}
                            >
                                <Button
                                    icon={<FaPlay color="green"/>}
                                    title="Deploys this pipeline"
                                    loading={deploying}
                                />
                            </Popconfirm>
                        }
                        {
                            <SlotPipelineDeploymentStatusButton
                                deploymentStatus={deploymentStatus}
                            />
                        }
                    </Space>
                </LoadingInline>
            }
        </>
    )
}