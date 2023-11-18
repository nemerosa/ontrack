import {Handle, Position} from "reactflow";
import {Card, Space, Tooltip, Typography} from "antd";
import {branchLink, branchUri, projectLink} from "@components/common/Links";
import {FaArrowCircleLeft, FaLink} from "react-icons/fa";
import Timestamp from "@components/common/Timestamp";
import PromotionRun from "@components/promotionRuns/PromotionRun";
import Link from "next/link";
import BuildRef from "@components/links/BuildRef";
import BuildPromotions from "@components/links/BuildPromotions";

function NodeSection({icon, title, children}) {
    return (
        <>
            <Space
                direction="vertical"
                style={{
                    borderTop: "solid 1px gray",
                    padding: '8px',
                }}
            >
                <Space>
                    {icon}
                    {title}
                </Space>
                {children}
            </Space>
        </>
    )
}

export default function BranchNode({data}) {

    const {branch, selected} = data

    const linkToBranchLinks = <Link
        href={`${branchUri(branch)}/links`}
        title="Links for this branch"
    ><FaLink/></Link>

    const latestBuild = branch.latestBuilds ? branch.latestBuilds[0] : undefined

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Handle type="source" position={Position.Right}/>
            <Handle type="target" position={Position.Top}/>
            <Handle type="source" position={Position.Bottom}/>
            <Card
                title={undefined}
                size="small"
                style={
                    selected ? {
                        border: 'solid 5px blue'
                    } : {
                        border: 'solid 2px black'
                    }
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
                                {branchLink(branch, branch.displayName)}
                                &nbsp;
                                {linkToBranchLinks}
                            </Typography.Text>
                        </Tooltip>
                    }
                    {
                        branch && (!branch.displayName || branch.displayName === branch.name) &&
                        <Typography.Text italic ellipsis>
                            {branchLink(branch, branch.name)}
                            &nbsp;
                            {linkToBranchLinks}
                        </Typography.Text>
                    }
                    {
                        latestBuild &&
                        <NodeSection
                            icon={<FaArrowCircleLeft/>}
                            title="Latest build"
                        >
                            <BuildRef build={latestBuild}/>
                            <Timestamp value={latestBuild?.creation?.time}/>
                            <BuildPromotions build={latestBuild}/>
                        </NodeSection>
                    }
                </Space>
            </Card>
        </>
    )
}