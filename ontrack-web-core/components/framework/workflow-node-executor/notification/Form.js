import {Form, Input} from "antd";
import {useState} from "react";
import SelectNotificationChannel from "@components/extension/notifications/SelectNotificationChannel";
import Well from "@components/common/Well";
import NotificationChannelConfigForm from "@components/extension/notifications/NotificationChannelConfigForm";

export default function NotificationWorkflowNodeExecutorForm() {

    const [channelType, setChannelType] = useState('')

    const onSelectedNotificationChannel = (channelType) => {
        setChannelType(channelType)
    }

    return (
        <>
            {/*  Channel selection  */}
            <Form.Item
                name={['data', 'channel']}
                label="Channel"
                rules={[{required: true, message: 'Channel is required.'}]}
            >
                <SelectNotificationChannel onSelectedNotificationChannel={onSelectedNotificationChannel}/>
            </Form.Item>
            {/*  Channel config form  */}
            {
                channelType &&
                <Form.Item
                    label="Channel config"
                >
                    <Well>
                        <NotificationChannelConfigForm
                            prefix={["data", "channelConfig"]}
                            channelType={channelType}
                        />
                    </Well>
                </Form.Item>
            }
            {/*  Content template  */}
            <Form.Item
                name={['data', 'template']}
                label="Custom template"
            >
                <Input.TextArea rows={5}/>
            </Form.Item>
        </>
    )

}