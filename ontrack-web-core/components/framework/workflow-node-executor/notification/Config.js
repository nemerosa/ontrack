import {Descriptions} from "antd";
import NotificationChannelConfig from "@components/extension/notifications/NotificationChannelConfig";

export default function NotificationWorkflowNodeExecutorConfig({data}) {

    const {channel, channelConfig, template} = data

    return (
        <>
            <Descriptions
                column={12}
                items={[
                    {
                        key: 'channel',
                        label: 'Channel',
                        children: channel,
                        span: 12,
                    },
                    {
                        key: 'config',
                        label: 'Notification config',
                        children: <NotificationChannelConfig
                            channel={channel}
                            config={channelConfig}
                        />,
                        span: 12,
                    },
                    {
                        key: 'template',
                        label: 'Template',
                        children: template,
                        span: 12,
                    },
                ]}
            />
        </>
    )

}