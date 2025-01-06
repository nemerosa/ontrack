import {FaStop} from "react-icons/fa";
import {Button, message} from "antd";
import {useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useConfirmWithReason} from "@components/common/ConfirmWithReason";
import {processGraphQLErrors} from "@components/services/graphql-utils";

export default function SlotPipelineCancelButton({pipeline, onCancel, size}) {
    const [messageApi, contextHolder] = message.useMessage()
    const client = useGraphQLClient()
    const [cancelling, setCancelling] = useState(false)

    const cancel = async (reason) => {
        setCancelling(true)
        try {
            const data = await client.request(
                gql`
                    mutation CancelPipeline(
                        $id: String!,
                        $reason: String!,
                    ) {
                        cancelSlotPipeline(input: {
                            pipelineId: $id,
                            reason: $reason,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `,
                {
                    id: pipeline.id,
                    reason: reason ?? 'Cancelled manually',
                }
            )
            if (processGraphQLErrors(data, 'cancelSlotPipeline', messageApi)) {
                if (onCancel) onCancel()
            }
        } finally {
            setCancelling(false)
        }
    }

    const [cancelConfirm, cancelComponent] = useConfirmWithReason({
        onConfirm: cancel,
        question: "Are you sure you want to cancel this deployment?",
    })

    return (
        <>
            {cancelComponent}
            {contextHolder}
            {
                !pipeline.finished &&
                <Button
                    icon={<FaStop color="red"/>}
                    title="Cancels this deployment"
                    loading={cancelling}
                    onClick={cancelConfirm}
                    size={size}
                />
            }
        </>
    )
}