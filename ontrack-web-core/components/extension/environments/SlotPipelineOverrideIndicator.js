import {Popover, Space, Typography} from "antd";
import {FaExclamationCircle} from "react-icons/fa";
import TimestampText from "@components/common/TimestampText";

export default function SlotPipelineOverrideIndicator({container, id, message}) {
    return (
        <>
            {
                container.overridden && container.override &&
                <Popover
                    title={message}
                    content={
                        <>
                            <Space direction="vertical">
                                <Typography.Text>
                                    By {container.override.user} at <TimestampText
                                    value={container.override.timestamp}/>
                                </Typography.Text>
                                {
                                    container.override.message &&
                                    <Typography.Text type="secondary">
                                        {container.override.message}
                                    </Typography.Text>
                                }
                            </Space>
                        </>
                    }
                >
                    <FaExclamationCircle data-testid={`overridden-${id}`} color="red"/>
                </Popover>
            }
        </>
    )
}