import {Descriptions, Space, Typography} from "antd";

export default function Display({
                                    dockerImage,
                                    dockerCommand,
                                    commitMessage,
                                    config,
                                    workflow,
                                }) {
    const items = [
        {
            key: 'dockerImage',
            label: "Docker image",
            children: <Typography.Text code>{dockerImage}</Typography.Text>,
            span: 12,
        },
        {
            key: 'dockerCommand',
            label: "Docker command",
            children: <Typography.Text code>{dockerCommand}</Typography.Text>,
            span: 12,
        },
        {
            key: 'commitMessage',
            label: "Commit message",
            children: <Typography.Text code>{commitMessage}</Typography.Text>,
            span: 12,
        },
        {
            key: 'config',
            label: "Specific GitHub config",
            children: <Typography.Text code>{config}</Typography.Text>,
            span: 12,
        },
        {
            key: 'repository',
            label: "Specific GitHub repository for the workflow job",
            children: <Typography.Text code>{repository}</Typography.Text>,
            span: 12,
        },
        {
            key: 'branch',
            label: "Specific GitHub repository branch for the workflow job",
            children: <Typography.Text code>{branch}</Typography.Text>,
            span: 12,
        },
        {
            key: 'workflow',
            label: "Specific GitHub workflow job",
            children: <Typography.Text code>{workflow}</Typography.Text>,
            span: 12,
        },
    ]

    return (
        <>
            <Space direction="vertical">
                <Typography.Text code>github</Typography.Text>
                <Descriptions
                    items={items}
                    span={12}
                />
            </Space>
        </>
    )
}