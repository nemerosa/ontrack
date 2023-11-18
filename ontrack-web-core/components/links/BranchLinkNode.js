import {Handle, Position} from "reactflow";
import {Card, Space, Typography} from "antd";
import {FaArrowLeft, FaCheck, FaLink, FaTimes} from "react-icons/fa";
import BuildRef from "@components/links/BuildRef";
import BuildPromotions from "@components/links/BuildPromotions";

export default function BranchLinkNode({data}) {

    console.log({data})

    const {link, sourceBranch, targetBranch} = data
    const {qualifier, sourceBuild, targetBuild} = link

    const sourceBranchLatest = sourceBranch.latestBuilds ? sourceBranch.latestBuilds[0] : undefined
    const targetBranchLatest = targetBranch.latestBuilds ? targetBranch.latestBuilds[0] : undefined

    const latestOk = sourceBranchLatest && targetBranchLatest &&
        (sourceBranchLatest.id === sourceBuild.id) &&
        (targetBranchLatest.id === targetBuild.id)

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
                            qualifier &&
                            <Typography.Text>{qualifier}</Typography.Text>
                        }
                    </Space>
                    {/* Latest link */}
                    <Space>
                        <Space direction="vertical">
                            <BuildRef build={sourceBuild}/>
                            <BuildPromotions build={sourceBuild}/>
                        </Space>
                        <Space direction="vertical">
                            <FaArrowLeft/>
                            {
                                latestOk ?
                                    <FaCheck color="green"/> :
                                    <FaTimes color="red"/>
                            }
                        </Space>
                        <Space direction="vertical">
                            <BuildRef build={targetBuild}/>
                            <BuildPromotions build={targetBuild}/>
                        </Space>
                    </Space>
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}