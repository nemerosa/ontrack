import {Space, Typography} from "antd";
import MessageTypeIcon from "@components/common/MessageTypeIcon";

export default function Display({property}) {

    return (
        <>
            <Space>
                <MessageTypeIcon type={property.value.type}/>
                <Typography.Text>{property.value.text}</Typography.Text>
            </Space>
        </>
    )
}