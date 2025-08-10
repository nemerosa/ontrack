import {Button, Popconfirm} from "antd";
import {FaPlay} from "react-icons/fa";
import {useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {processGraphQLErrors} from "@components/services/graphql-utils";
import {useMessageApi} from "@components/providers/MessageProvider";

export default function SlotPipelineCreateButton({slot, build, size, onStart, title = "Creates a candidate deployment for this build", text}) {

    const messageApi = useMessageApi()
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)

    const onClick = async () => {
        setLoading(true)
        try {
            const data = await client.request(
                gql`
                    mutation StartPipeline(
                        $slotId: String!,
                        $buildId: Int!,
                    ) {
                        startSlotPipeline(input: {
                            slotId: $slotId,
                            buildId: $buildId,
                        }) {
                            pipeline {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                `,
                {
                    slotId: slot.id,
                    buildId: build.id,
                }
            )
            if (processGraphQLErrors(data, 'startSlotPipeline', messageApi)) {
                const pipelineId = data.startSlotPipeline?.pipeline?.id
                if (onStart && pipelineId) {
                    onStart(pipelineId)
                }
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <>
            <Popconfirm
                title="Candidate deployment"
                description="Creating a candidate deployment will cancel all currently active deployments for this slot. Are you sure to continue?"
                onConfirm={onClick}
            >
                <Button
                    icon={<FaPlay color="green"/>}
                    title={title}
                    loading={loading}
                    size={size}
                >
                    {text}
                </Button>
            </Popconfirm>
        </>
    )
}