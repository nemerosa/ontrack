import {Tag} from "antd";

export const slotWorkflowTriggers = {
    CREATION: "On creation",
    DEPLOYING: "When deploying",
    DEPLOYED: "When deployed",
}

export default function SlotWorkflowTrigger({trigger}) {
    const text = slotWorkflowTriggers[trigger]
    return (
        <>
            <Tag>{text}</Tag>
        </>
    )
}