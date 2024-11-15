import {slotWorkflowTriggers} from "@components/extension/environments/SlotWorkflowTrigger";
import {Select} from "antd";

export default function SelectSlotWorkflowTrigger({value, onChange}) {
    const triggers = slotWorkflowTriggers
    const options = Object.keys(triggers).map(value => {
        const label = triggers[value]
        return {value, label}
    })
    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
            />
        </>
    )
}