import {Handle, Position} from "reactflow";
import {Card, Space, Typography} from "antd";
import {FaLink} from "react-icons/fa";

export default function BranchLinkNode({data}) {

    const {link, sourceBranch} = data

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Card
                title={undefined}
                size="small"
                style={{
                    border: 'dashed 1px gray'
                }}
            >
                <Space direction="vertical">
                    <Space>
                        <FaLink/>
                        {
                            link.qualifier &&
                            <Typography.Text>{link.qualifier}</Typography.Text>
                        }
                    </Space>
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}