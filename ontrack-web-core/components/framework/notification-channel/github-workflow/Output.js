import {Descriptions, Typography} from "antd";
import Link from "next/link";

export default function GitHubWorkflowNotificationChannelOutput({url, owner, repository, workflowRunId, inputs}) {

    const runUrl = `${url}/${owner}/${repository}/actions/runs/${workflowRunId}`

    return (
        <>
            <Descriptions
                column={12}
                items={[
                    {
                        key: 'url',
                        label: 'Run URL',
                        children: <Link href={runUrl}>{runUrl}</Link>,
                        span: 12,
                    },
                    {
                        key: 'inputs',
                        label: 'Inputs',
                        children: <>
                            <Descriptions
                                items={inputs.map(({name, value}) => (
                                    {
                                        key: name,
                                        label: name,
                                        children: <code>{value}</code>,
                                        span: 12,
                                    }
                                ))}
                            />
                            {
                                inputs.length === 0 && <Typography.Text type="secondary">None</Typography.Text>
                            }
                        </>,
                        span: 12,
                    }
                ]}
            />
        </>
    )
}