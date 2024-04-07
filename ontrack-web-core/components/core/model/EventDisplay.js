import {useRefData} from "@components/providers/RefDataProvider";
import {Space, Tag, Typography} from "antd";

export default function EventDisplay({event}) {

    const {eventTypes} = useRefData()

    const eventDescription = eventTypes.find(it => it.id === event)?.description

    return (
        <Space>
            <Tag>{event}</Tag>
            <Typography.Text>{eventDescription}</Typography.Text>
        </Space>
    )

}