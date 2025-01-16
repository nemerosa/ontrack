import {Button} from "antd";
import {FaExclamationCircle} from "react-icons/fa";
import SlotPipelineOverrideRuleDialog, {
    useSlotPipelineOverrideRuleDialog
} from "@components/extension/environments/SlotPipelineOverrideRuleDialog";

export default function SlotPipelineOverrideRuleButton({pipeline, rule, onChange}) {

    const dialog = useSlotPipelineOverrideRuleDialog({
        onSuccess: onChange,
    })

    const onOverride = () => {
        dialog.start({pipeline, rule})
    }

    return (
        <>
            {
                !rule.check.ok && rule.override === null && rule.canBeOverridden && <>
                    <Button
                        title="Overriding this rule"
                        data-testid={`override-${rule.admissionRuleConfig.id}`}
                        onClick={onOverride}
                    >
                        <FaExclamationCircle color="red"/>
                    </Button>
                    <SlotPipelineOverrideRuleDialog dialog={dialog}/>
                </>
            }
        </>
    )
}