import {Handle, Position} from "reactflow";
import {Card, Space, Typography} from "antd";
import {FaArrowLeft, FaCaretRight, FaCheck, FaLink, FaMagic, FaTimes} from "react-icons/fa";
import BuildRef from "@components/links/BuildRef";
import BuildPromotions from "@components/links/BuildPromotions";
import AutoVersioningInfo from "@components/links/AutoVersioningInfo";
import {NodeSection} from "@components/links/NodeSection";
import CheckIcon from "@components/common/CheckIcon";

export default function BranchLinkNode({data}) {

    console.log({data})

    const {link, sourceBranch, targetBranch} = data
    const {qualifier, sourceBuild, targetBuild, autoVersioning} = link

    const sourceBranchLatest = sourceBranch.latestBuilds ? sourceBranch.latestBuilds[0] : undefined
    const targetBranchLatest = targetBranch.latestBuilds ? targetBranch.latestBuilds[0] : undefined

    const latestOk = sourceBranchLatest && targetBranchLatest &&
        (sourceBranchLatest.id === sourceBuild.id) &&
        (targetBranchLatest.id === targetBuild.id)

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Handle type="source" position={Position.Right}/>
            <Handle type="target" position={Position.Top}/>
            <Handle type="source" position={Position.Bottom}/>
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
                    <NodeSection
                        icon={<FaCaretRight/>}
                        title="Latest link"
                    >
                        <Space>
                            <Space direction="vertical">
                                <BuildRef build={sourceBuild}/>
                                <BuildPromotions build={sourceBuild}/>
                            </Space>
                            <Space direction="vertical">
                                <FaArrowLeft/>
                                <CheckIcon value={latestOk}/>
                            </Space>
                            <Space direction="vertical">
                                <BuildRef build={targetBuild}/>
                                <BuildPromotions build={targetBuild}/>
                            </Space>
                        </Space>
                    </NodeSection>
                    {/* AV information */}
                    {
                        autoVersioning &&
                        <NodeSection
                            icon={<FaMagic/>}
                            title="Auto versioning"
                        >
                            <AutoVersioningInfo
                                autoVersioning={autoVersioning}
                                branchLink={link}
                            />
                        </NodeSection>
                    }
                </Space>
            </Card>
        </>
    )
}