import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useState} from "react";

export default function SlotWorkflowDeleteButton({slot, slotWorkflow, onChange}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const deleteWorkflow = async () => {
        setLoading(true)
        try {
            await client.request(
                gql`
                    mutation DeleteSlotWorkflow($slotWorkflowId: String!) {
                        deleteSlotWorkflow(input: {id: $slotWorkflowId}) {
                            errors {
                                message
                            }
                        }
                    }
                `,
                {slotWorkflowId: slotWorkflow.id}
            )
            if (onChange) onChange()
        } finally {
            setLoading(false)
        }
    }

    return (
        <>
            <InlineConfirmCommand
                title="Delete this workflow"
                confirm="Do you really want to delete this workflow?"
                onConfirm={deleteWorkflow}
                loading={loading}
            />
        </>
    )
}