import {Handle, Position} from "reactflow";
import {Card, Space, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import Timestamp from "@components/common/Timestamp";
import BuildPromotions from "@components/links/BuildPromotions";
import BranchDisplayNameLink from "@components/links/BranchDisplayNameLink";
import ProjectLink from "@components/projects/ProjectLink";

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
                <Space direction="vertical">
                    <Typography.Text ellipsis={true}>
                        {build && <ProjectLink project={build.branch.project}/>}
                    </Typography.Text>
                    {
                        build && <BranchDisplayNameLink branch={build.branch}/>
                    }
                    <Typography.Text strong>
                        {build && <BuildLink build={build}></BuildLink>}
                    </Typography.Text>
                    <Timestamp value={build?.creation?.time}/>
                    <BuildPromotions build={build}/>
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}