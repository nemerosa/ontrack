import LoadingInline from "@components/common/LoadingInline";
import {Button, Space} from "antd";
import {FaHandPaper} from "react-icons/fa";
import {gql} from "graphql-request";
import SlotPipelineInputDialog, {
    useSlotPipelineInputDialog
} from "@components/extension/environments/SlotPipelineInputDialog";
import {useReloadState} from "@components/common/StateUtils";
import {useQuery} from "@components/services/useQuery";

export default function SlotPipelineInputButton({pipeline, onChange, size}) {
    const [reloadState, reload] = useReloadState()

    const onDataChange = () => {
        reload()
        if (onChange) onChange()
    }

    const {data, loading} = useQuery(
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
        {
            variables: {id: pipeline.id},
            // deps: pipeline.id,
            deps: [pipeline.id, reloadState],
            dataFn: (data) => {
                const inputs = data.slotPipelineById.requiredInputs
                return inputs && inputs.length > 0
            }
        }
    )

    const dialog = useSlotPipelineInputDialog()
    const pipelineInput = async () => {
        dialog.start({pipeline, onChange: onDataChange})
    }

    return (
        <>
            <LoadingInline loading={loading}>
                {
                    data &&
                    <Button
                        data-testid="pipeline-input-needed"
                        title="Some input is needed for this deployment"
                        onClick={pipelineInput}
                        size={size}
                    >
                        <Space>
                            <FaHandPaper color="orange"/>
                        </Space>
                    </Button>
                }
            </LoadingInline>
            <SlotPipelineInputDialog dialog={dialog}/>
        </>
    )
}