import {Handle, Position} from "reactflow";
import {Card, Space, Tooltip, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import Timestamp from "@components/common/Timestamp";
import BuildPromotions from "@components/links/BuildPromotions";
import BranchDisplayNameLink from "@components/links/BranchDisplayNameLink";
import ProjectLink from "@components/projects/ProjectLink";
import Link from "next/link";
import {branchLinksUri, branchUri} from "@components/common/Links";
import {FaProjectDiagram} from "react-icons/fa";

export default function BuildNode({data}) {

    const {build, selected} = data

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
                <Space direction="vertical" className="ot-line">
                    <Typography.Text>
                        {build && <ProjectLink project={build.branch.project} shorten={true}/>}
                    </Typography.Text>
                    {
                        build && <Space>
                            <BranchDisplayNameLink branch={build.branch}/>
                            <Tooltip title="Graph of branch links">
                                <Link href={branchLinksUri(build.branch)}><FaProjectDiagram size="12"/></Link>
                            </Tooltip>
                        </Space>
                    }
                    <Typography.Text strong>
                        {build && <BuildLink build={build} displayTooltip={true}></BuildLink>}
                    </Typography.Text>
                    <Timestamp value={build?.creation?.time}/>
                    <BuildPromotions build={build}/>
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}