import {Handle, Position} from "reactflow";
import {Card, Space, Tooltip, Typography} from "antd";
import {branchLink, projectLink} from "@components/common/Links";

export default function BranchNode({data}) {

    const {branch, selected} = data

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Card
                title={undefined}
                size="small"
                style={
                    selected ? {
                        border: 'solid 3px blue'
                    } : {}
                }
            >
                <Space direction="vertical">
                    <Typography.Text ellipsis={true}>
                        {branch && projectLink(branch.project)}
                    </Typography.Text>
                    {
                        branch && branch.displayName && branch.displayName !== branch.name &&
                        <Tooltip title={branch.name}>
                            <Typography.Text italic ellipsis>
                                {branch.displayName}
                            </Typography.Text>
                        </Tooltip>
                    }
                    {
                        branch && (!branch.displayName || branch.displayName === branch.name) &&
                        <Typography.Text italic ellipsis>
                            {branch.name}
                        </Typography.Text>
                    }
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}