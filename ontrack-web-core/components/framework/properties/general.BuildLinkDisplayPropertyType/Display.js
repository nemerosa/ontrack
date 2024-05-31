import {Descriptions, Space, Tag, Typography} from "antd";
import TimestampText from "@components/common/TimestampText";
import YesNo from "@components/common/YesNo";

export default function Display({property}) {
    return (
        <Space>
            <Typography.Text>Use label:</Typography.Text>
            <YesNo strong={true} value={property.value.useLabel}/>
        </Space>
    )
}