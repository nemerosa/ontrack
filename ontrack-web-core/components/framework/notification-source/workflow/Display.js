import Link from "next/link";

export default function WorkflowNotificationSource({workflowInstanceId}) {
    return (
        <>
            <Link href={`/extension/workflows/instances/${workflowInstanceId}`}>Workflow</Link>
        </>

    )
}