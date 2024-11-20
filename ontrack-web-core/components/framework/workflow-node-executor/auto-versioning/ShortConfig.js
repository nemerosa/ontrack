export default function AutoVersioningWorkflowNodeExecutorShortConfig({data}) {

    const {targetProject, targetPath} = data

    return (
        <>
            {targetProject} @ {targetPath}
        </>
    )

}