import {FaInfoCircle} from "react-icons/fa";
import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotPipelineDeploymentStatusButton({pipeline}) {
    return (
        <>
            <Link href={slotPipelineUri(pipeline.id)} title={`Pipeline #${pipeline.number} details`}>
                <FaInfoCircle className="ot-action"/>
            </Link>
        </>
    )
}