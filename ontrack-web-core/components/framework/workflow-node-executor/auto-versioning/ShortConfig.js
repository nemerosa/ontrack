export default function AutoVersioningWorkflowNodeExecutorShortConfig({data}) {

    const {targetProject, targetBranch, targetPath} = data

    return (
        <>
            {JSON.stringify(data)}
        </>
    )

}