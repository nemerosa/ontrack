import {Descriptions} from "antd";
import JsonDisplay from "@components/common/JsonDisplay";

export default function WebhookNotificationChannelOutput({payload}) {
    return (
        <>
            <Descriptions items={[
                {
                    key: 'payload',
                    label: 'Payload',
                    children: <JsonDisplay
                        value={JSON.stringify(payload, null, 3)}
                        width="600px"
                    />,
                }
            ]}/>
        </>
    )
}