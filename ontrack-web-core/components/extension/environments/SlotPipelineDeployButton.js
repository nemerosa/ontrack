import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Button, Popconfirm, Space} from "antd";
import {FaInfoCircle, FaPlay} from "react-icons/fa";
import LoadingInline from "@components/common/LoadingInline";
import {gql} from "graphql-request";
import SlotPipelineDeploymentStatusButton from "@components/extension/environments/SlotPipelineDeploymentStatusButton";

export default function SlotPipelineDeployButton({pipeline}) {

    const client = useGraphQLClient()

    const [loadingDeployable, setLoadingDeployable] = useState(true)
    const [deploymentStatus, setDeploymentStatus] = useState()

    useEffect(() => {
        if (client && pipeline) {
            setLoadingDeployable(true)
            client.request(
                gql`
                    query PipelineDeployable($id: String!) {
                        slotPipelineById(id: $id) {
                            deploymentStatus {
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
                        }
                    }
                `,
                {id: pipeline.id}
            ).then(data => {
                setDeploymentStatus(data.slotPipelineById?.deploymentStatus)
            }).finally(() => {
                setLoadingDeployable(false)
            })
        }
    }, [client, pipeline])

    const deploy = () => {
    }

    return (
        <>
            {
                pipeline.status === 'ONGOING' && deploymentStatus &&
                <LoadingInline loading={loadingDeployable} text="">
                    <Space>
                        {
                            !deploymentStatus.status &&
                            <Button
                                icon={<FaInfoCircle color="green"/>}
                                title="Deploys this pipeline"
                                loading={loadingDeployable}
                            />
                        }
                        {
                            deploymentStatus.status &&
                            <Popconfirm
                                title="Deploying pipeline"
                                description="Deploying this pipeline may trigger an event to perform an actual deployment. Do you want to continue?"
                                onConfirm={deploy}
                            >
                                <Button
                                    icon={<FaPlay color="green"/>}
                                    title="Deploys this pipeline"
                                    loading={loadingDeployable}
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