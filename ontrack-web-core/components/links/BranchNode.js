import {Handle, Position} from "reactflow";
import {Card, Space, Tooltip, Typography} from "antd";
import {buildLink, projectLink} from "@components/common/Links";
import {FaArrowCircleLeft} from "react-icons/fa";

export default function BranchNode({data}) {

    const {branch, selected} = data

    const latestBuild = branch.latestBuilds ? branch.latestBuilds[0] : undefined

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
                    {
                        latestBuild &&
                        <Space>
                            <Typography.Text italic>Latest</Typography.Text>
                            <FaArrowCircleLeft/>
                            {buildLink(latestBuild)}
                        </Space>
                    }
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}