import {List, Space} from "antd";
import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";
import {FaPlay, FaStop} from "react-icons/fa";
import TimestampText from "@components/common/TimestampText";

export default function BuildDeploymentListItem({deployment}) {
    return (
        <>
            <List.Item
                key={deployment.id}
            >
                <List.Item.Meta
                    avatar={
                        <SlotPipelineStatusIcon
                            status={deployment.status}
                        />
                    }
                    title={
                        <Space>
                            Pipeline
                            <SlotPipelineLink
                                pipelineId={deployment.id}
                                numberOnly={true}
                            />
                        </Space>
                    }
                    description={
                        <Space direction="vertical">
                            {
                                deployment.end &&
                                <Space>
                                    <FaStop/>
                                    <TimestampText value={deployment.end}/>
                                </Space>
                            }
                            {
                                !deployment.end &&
                                <Space>
                                    <FaPlay/>
                                    <TimestampText
                                        value={deployment.start}/>
                                </Space>
                            }
                        </Space>
                    }
                />
            </List.Item>
        </>
    )
}
