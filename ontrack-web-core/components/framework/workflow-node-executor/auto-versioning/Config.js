import AutoVersioningConfigDetails from "@components/extension/auto-versioning/AutoVersioningConfigDetails";

export default function AutoVersioningWorkflowNodeExecutorConfig({data}) {
    return (
        <>
            <AutoVersioningConfigDetails
                source={data}
                size="small"
                additionalItems={[
                    {
                        key: 'targetProject',
                        label: "Target project",
                        children: data.targetProject,
                        span: 6,
                    },
                    {
                        key: 'targetBranch',
                        label: "Target branch",
                        children: data.targetBranch,
                        span: 6,
                    },
                ]
                }
            />
        </>
    )

}