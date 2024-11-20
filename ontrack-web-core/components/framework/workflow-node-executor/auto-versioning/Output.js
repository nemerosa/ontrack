import AutoVersioningAuditEntryLink from "@components/extension/auto-versioning/AutoVersioningAuditEntryLink";

export default function AutoVersioningWorkflowNodeExecutorOutput({data, nodeData}) {

    const {autoVersioningOrderId} = data

    return (
        <>
            <AutoVersioningAuditEntryLink uuid={autoVersioningOrderId}/>
        </>
    )
}