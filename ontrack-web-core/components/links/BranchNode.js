import {Handle, Position} from "reactflow";
import {Card, Space, Typography} from "antd";
import {branchUri} from "@components/common/Links";
import {FaArrowCircleLeft, FaLink} from "react-icons/fa";
import Timestamp from "@components/common/Timestamp";
import Link from "next/link";
import BuildRef from "@components/links/BuildRef";
import BuildPromotions from "@components/links/BuildPromotions";
import BranchDisplayNameLink from "@components/links/BranchDisplayNameLink";
import {NodeSection} from "@components/links/NodeSection";
import ProjectLink from "@components/projects/ProjectLink";

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
                <Space direction="vertical" className="ot-line">
                    <Typography.Text>
                        {branch && <ProjectLink project={branch.project} shorten={true}/>}
                    </Typography.Text>
                    <BranchDisplayNameLink branch={branch}>
                        &nbsp;
                        {linkToBranchLinks}
                    </BranchDisplayNameLink>
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