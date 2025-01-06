import {Tag} from "antd";

export const slotWorkflowTriggers = {
    CANDIDATE: "On candidate",
    RUNNING: "On running",
    DONE: "On deployment done",
}

export default function SlotWorkflowTrigger({trigger}) {
    const text = slotWorkflowTriggers[trigger]
    return (
        <>
            <Tag>{text}</Tag>
        </>
    )
}