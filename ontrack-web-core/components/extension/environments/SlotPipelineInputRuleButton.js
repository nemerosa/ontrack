import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import LoadingInline from "@components/common/LoadingInline";
import {FaHandPaper} from "react-icons/fa";
import {Button} from "antd";
import SlotPipelineInputDialog, {
    useSlotPipelineInputDialog
} from "@components/extension/environments/SlotPipelineInputDialog";
import {useReloadState} from "@components/common/StateUtils";

export default function SlotPipelineInputRuleButton({pipelineId, ruleConfigId, onChange}) {

    const dialog = useSlotPipelineInputDialog(ruleConfigId)

    const [reloadState, reload] = useReloadState({
        callback: onChange,
    })

    const {data: isRequiredInput, loading} = useQuery(
        gql`
            query PipelineInputs($pipelineId: String!) {
                slotPipelineById(id: $pipelineId) {
                    requiredInputs {
                        config {
                            id
                        }
                    }
                }
            }
        `,
        {
            variables: {
                pipelineId: pipelineId,
            },
            deps: [pipelineId, reloadState],
            dataFn: (data) => {
                return data.slotPipelineById.requiredInputs.find(it => it.config.id === ruleConfigId) !== undefined
            }
        }
    )

    const onClick = () => {
        dialog.start({
            pipeline: {
                id: pipelineId,
            },
            onChange: reload,
        })
    }

    return (
        <>
            <LoadingInline loading={loading} text="">
                {
                    isRequiredInput &&
                    <Button
                        data-testid={`pipeline-rule-input-${ruleConfigId}`}
                        onClick={onClick}
                    >
                        <FaHandPaper color="orange"/>
                    </Button>
                }
            </LoadingInline>
            <SlotPipelineInputDialog dialog={dialog}/>
        </>
    )
}