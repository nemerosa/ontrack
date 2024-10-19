import {Button, Popconfirm} from "antd";
import {FaPlay} from "react-icons/fa";
import {useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export default function SlotPipelineCreateButton({slot, build}) {

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
        } finally {
            setLoading(false)
        }
    }

    return (
        <>
            <Popconfirm
                title="Starting pipeline"
                description="Starting this pipeline will cancel all currently active pipelines for this slot. Are you sure to continue?"
                onConfirm={onClick}
            >
                <Button
                    icon={<FaPlay color="green"/>}
                    title="Starts a pipeline for this build"
                    loading={loading}
                />
            </Popconfirm>
        </>
    )
}