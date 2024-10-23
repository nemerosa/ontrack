import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Empty, Form, message, Space} from "antd";
import SelectSlot from "@components/extension/environments/SelectSlot";
import {useEffect, useState} from "react";
import LoadingContainer from "@components/common/LoadingContainer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlSlotData} from "@components/extension/environments/EnvironmentGraphQL";
import SlotCurrentPipeline from "@components/extension/environments/SlotCurrentPipeline";
import {buildKnownName} from "@components/common/Titles";
import Link from "next/link";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import SlotDetails from "@components/extension/environments/SlotDetails";
import {FaPlay} from "react-icons/fa";

export const useBuildStartDeploymentDialog = ({buildId}) => {

    const [messageApi, contextHolder] = message.useMessage()
    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [build, setBuild] = useState()
    const [eligibleSlots, setEligibleSlots] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query GetEligibleSlots($buildId: Int!) {
                        build(id: $buildId) {
                            name
                            releaseProperty {
                                value
                            }
                        }
                        eligibleSlotsForBuild(buildId: $buildId) {
                            eligible
                            slot {
                                ...SlotData
                                environment {
                                    id
                                    name
                                }
                            }
                        }
                    }
                    ${gqlSlotData}
                `,
                {buildId},
            ).then(data => {
                setBuild(data.build)
                setEligibleSlots(data.eligibleSlotsForBuild)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, buildId])

    return useFormDialog({
        loading,
        eligibleSlots,
        contextHolder,
        init: (form) => {
            form.setFieldValue('slot', null)
        },
        prepareValues: (values) => {
            return {
                slotId: values.slot,
                buildId,
            }
        },
        query: gql`
            mutation StartBuildPipeline(
                $slotId: String!,
                $buildId: Int!,
            ) {
                startSlotPipeline(input: {
                    slotId: $slotId,
                    buildId: $buildId,
                }) {
                    pipeline {
                        slot {
                            id
                            qualifier
                            environment {
                                name
                            }
                        }
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'startSlotPipeline',
        onSuccess: async (result) => {
            messageApi.success(
                <>
                    Build {buildKnownName(build)} deployment
                    has started. Follow the pipeline at <Link href={slotUri({id: result.pipeline.slot.id})}>
                    {result.pipeline.slot.environment.name}
                    {
                        result.pipeline.slot.qualifier &&
                        ` [${result.pipeline.slot.qualifier}]`
                    }
                </Link>
                </>,
                15,
            )
        }
    })
}

export default function BuildStartDeploymentDialog({dialog}) {

    const [slotId, setSlotId] = useState(null)

    const onValuesChange = (changedValues /*, allValues*/) => {
        setSlotId(changedValues['slot'])
    }

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(false)
    const [slot, setSlot] = useState(null)

    useEffect(() => {
        if (client) {
            if (slotId) {
                setLoading(true)
                client.request(
                    gql`
                        query SlotById($id: String!) {
                            slotById(id: $id) {
                                ...SlotData
                                environment {
                                    name
                                }
                            }
                        }
                        ${gqlSlotData}
                    `,
                    {id: slotId}
                ).then(data => {
                    setSlot(data.slotById)
                }).finally(() => {
                    setLoading(false)
                })
            } else {
                setSlot(null)
            }
        }
    }, [client, slotId])

    return (
        <>
            {dialog.contextHolder}
            <FormDialog
                dialog={dialog}
                submittable={!dialog.loading}
                onValuesChange={onValuesChange}
                okText={
                    <Space>
                        <FaPlay color="white"/>
                        Start pipeline
                    </Space>
                }
            >
                <LoadingContainer loading={dialog.loading}>
                    <Form.Item
                        name="slot"
                        label="Slot to deploy to"
                        rules={[
                            {
                                required: true,
                                message: 'Slot destination is required.',
                            },
                        ]}
                    >
                        <SelectSlot eligibleSlots={dialog.eligibleSlots}/>
                    </Form.Item>
                    {
                        slotId &&
                        <LoadingContainer loading={loading}>
                            <Form.Item>
                                {
                                    slot &&
                                    <Space direction="vertical">
                                        <SlotDetails slot={slot}/>
                                        <SlotCurrentPipeline
                                            slot={slot}
                                            actions={false}
                                            titlePrefix="Current: "
                                            showLastDeployed={true}
                                        />
                                    </Space>
                                }
                                {
                                    !slot && <Empty
                                        description="A slot must be selected for the deployment"
                                    />
                                }
                            </Form.Item>
                        </LoadingContainer>
                    }
                </LoadingContainer>
            </FormDialog>
        </>
    )
}