import {Button} from "antd";
import {FaExclamationCircle} from "react-icons/fa";
import SlotPipelineOverrideRuleDialog, {
    useSlotPipelineOverrideRuleDialog
} from "@components/extension/environments/SlotPipelineOverrideRuleDialog";

export default function SlotPipelineOverrideRuleButton({pipeline, check, onChange}) {

    const dialog = useSlotPipelineOverrideRuleDialog({
        onSuccess: onChange,
    })

    const onOverride = () => {
        dialog.start({pipeline, check})
    }

    return (
        <>
            {
                !check.override?.override && check.canBeOverridden && <>
                    <Button
                        icon={<FaExclamationCircle color="red"/>}
                        title="Overriding this rule"
                        data-testid={`override-${check.config.name}`}
                        onClick={onOverride}
                    />
                    <SlotPipelineOverrideRuleDialog dialog={dialog}/>
                </>
            }
        </>
    )
}