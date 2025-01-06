import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import LoadingInline from "@components/common/LoadingInline";
import {Button} from "antd";
import {FaHandPaper} from "react-icons/fa";
import {gql} from "graphql-request";
import SlotPipelineInputDialog, {
    useSlotPipelineInputDialog
} from "@components/extension/environments/SlotPipelineInputDialog";
import {useReloadState} from "@components/common/StateUtils";

export default function SlotPipelineInputButton({pipeline, onChange, size}) {
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [inputNeeded, setInputNeeded] = useState(false)

    const [reloadState, reload] = useReloadState()

    const onDataChange = () => {
        reload()
        if (onChange) onChange()
    }

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query PipelineInput($id: String!) {
                        slotPipelineById(id: $id) {
                            requiredInputs {
                                # Only the list of inputs is needed, not the details
                                config {
                                    id
                                }
                            }
                        }
                    }
                `,
                {id: pipeline.id}
            ).then(data => {
                const inputs = data.slotPipelineById.requiredInputs
                setInputNeeded(inputs && inputs.length > 0)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, pipeline.id, reloadState])

    const dialog = useSlotPipelineInputDialog()
    const pipelineInput = async () => {
        dialog.start({pipeline, onChange: onDataChange})
    }

    return (
        <>
            <LoadingInline loading={loading}>
                {
                    inputNeeded &&
                    <Button
                        data-testid="pipeline-input-needed"
                        icon={<FaHandPaper color="orange"/>}
                        title="Some input is needed for this deployment"
                        onClick={pipelineInput}
                        size={size}
                    />
                }
            </LoadingInline>
            <SlotPipelineInputDialog dialog={dialog}/>
        </>
    )
}