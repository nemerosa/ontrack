import {Descriptions, Space, Typography} from "antd";
import CheckIcon from "@components/common/CheckIcon";

export default function InMemoryNotificationChannelOutput({sent}) {
    return (
        <>
            <Descriptions items={[
                {
                    key: 'sent',
                    label: 'Sent',
                    children: <CheckIcon value={sent}/>,
                }
            ]}/>
        </>
    )
}