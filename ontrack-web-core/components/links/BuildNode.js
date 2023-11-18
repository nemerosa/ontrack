import {Handle, Position} from "reactflow";
import {Card, Space, Typography} from "antd";
import {branchLink, projectLink} from "@components/common/Links";
import BuildLink from "@components/builds/BuildLink";
import PromotionRun from "@components/promotionRuns/PromotionRun";
import Timestamp from "@components/common/Timestamp";

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
                        {build && projectLink(build.branch.project)}
                    </Typography.Text>
                    <Typography.Text ellipsis={true} italic>
                        {build && branchLink(build.branch)}
                    </Typography.Text>
                    <Typography.Text strong>
                        {build && <BuildLink build={build}></BuildLink>}
                    </Typography.Text>
                    <Timestamp value={build?.creation?.time}/>
                    <Space size={8}>
                        {
                            build.promotionRuns.map(promotionRun =>
                                <PromotionRun
                                    key={promotionRun.id}
                                    promotionRun={promotionRun}
                                    size={16}
                                />
                            )
                        }
                    </Space>
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}