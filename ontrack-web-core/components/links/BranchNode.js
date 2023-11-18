import {Handle, Position} from "reactflow";
import {Card, Space, Tooltip, Typography} from "antd";
import {branchLink, branchUri, buildLink, projectLink} from "@components/common/Links";
import {FaArrowCircleLeft, FaLink} from "react-icons/fa";
import Timestamp from "@components/common/Timestamp";
import PromotionRun from "@components/promotionRuns/PromotionRun";
import Link from "next/link";

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
    let latestBuildText = <Typography.Text ellipsis>{latestBuild?.name}</Typography.Text>
    if (latestBuild?.releaseProperty?.value) {
        latestBuildText = <Tooltip title={latestBuild?.name}>{latestBuildText}</Tooltip>
    }

    return (
        <>
            <Handle type="target" position={Position.Left}/>
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
                            {buildLink(latestBuild, latestBuildText)}
                            <Timestamp value={latestBuild?.creation?.time}/>
                            <Space size={8}>
                                {
                                    latestBuild.promotionRuns.map(promotionRun =>
                                        <PromotionRun
                                            key={promotionRun.id}
                                            promotionRun={promotionRun}
                                            size={16}
                                        />
                                    )
                                }
                            </Space>
                        </NodeSection>
                    }
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}