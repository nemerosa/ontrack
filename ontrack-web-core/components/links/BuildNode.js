import {Handle, Position} from "reactflow";
import {Card, Popover, Space, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import BuildPromotions from "@components/links/BuildPromotions";
import ProjectLink from "@components/projects/ProjectLink";
import {FaInfoCircle} from "react-icons/fa";
import BuildNodePopoverContent from "@components/links/BuildNodePopoverContent";

export default function BuildNode({data}) {

    const {build, selected} = data

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Card
                data-testid={`ot-build-link-node-${build.id}`}
                title={undefined}
                size="small"
                style={
                    selected ? {
                        border: 'solid 3px blue'
                    } : {
                        border: 'solid 2px #777'
                    }
                }
                bodyStyle={{
                    overflow: 'hidden'
                }}
            >
                <Space direction="vertical" className="ot-line">
                    <Typography.Text>
                        {build && <ProjectLink project={build.branch.project} shorten={false}/>}
                    </Typography.Text>
                    <Typography.Text strong>
                        {
                            build && <Space>
                                <BuildLink build={build} displayTooltip={false}></BuildLink>
                                <BuildPromotions build={build} lastOnly={true}/>
                                <Popover
                                    content={<BuildNodePopoverContent build={build}/>}
                                >
                                    <FaInfoCircle color="blue"/>
                                </Popover>
                            </Space>
                        }
                    </Typography.Text>
                </Space>
            </Card>
            <Handle type="source" position={Position.Right}/>
        </>
    )
}