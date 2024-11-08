import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotPipelineCreationWorkflowNodeExecutorOutput({data, nodeData}) {

    const {targetPipelineId} = data

    return (
        <>
            <Link href={slotPipelineUri(targetPipelineId)}>
                TODO Loads the pipeline details
            </Link>
        </>
    )
}