import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import SelectMultipleEvents from "@components/core/model/SelectMultipleEvents";
import SelectNotificationChannel from "@components/extension/notifications/SelectNotificationChannel";
import {useState} from "react";
import Well from "@components/common/Well";
import NotificationChannelConfigForm from "@components/extension/notifications/NotificationChannelConfigForm";
import {gql} from "graphql-request";

export const useSubscriptionDialog = ({onSuccess, projectEntity}) => {

    const [channelType, setChannelType] = useState('')

    return useFormDialog({
        onSuccess,
        channelType, setChannelType,
        prepareValues: (values) => {
            console.log({values})
            return {
                ...values,
                channel: channelType,
                projectEntity,
            }
        },
        query: gql`
            mutation CreateSubscription(
                $projectEntity: ProjectEntityIDInput,
                $events: [String!]!,
                $keywords: String,
                $channel: String!,
                $channelConfig: JSON!,
                $contentTemplate: String,
            ) {
                subscribeToEvents(input: {
                    projectEntity: $projectEntity,
                    events: $events,
                    keywords: $keywords,
                    channel: $channel,
                    channelConfig: $channelConfig,
                    contentTemplate: $contentTemplate,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'subscribeToEvents'
    })
}

export default function SubscriptionDialog({subscriptionDialog}) {

    const onSelectedNotificationChannel = (channelType) => {
        subscriptionDialog.setChannelType(channelType)
    }

    return (
        <>
            <FormDialog dialog={subscriptionDialog}>
                {/*  Event  */}
                <Form.Item
                    name="events"
                    label="Events"
                    extra="Events to subscribe to"
                    rules={[{required: true, message: 'At least one event is required.'}]}
                >
                    <SelectMultipleEvents/>
                </Form.Item>
                {/*  Keywords  */}
                <Form.Item
                    name="keywords"
                    label="Keywords"
                >
                    <Input/>
                </Form.Item>
                {/*  Channel selection  */}
                <Form.Item
                    name="channel"
                    label="Channel"
                    rules={[{required: true, message: 'Channel is required.'}]}
                >
                    <SelectNotificationChannel onSelectedNotificationChannel={onSelectedNotificationChannel}/>
                </Form.Item>
                {/*  Channel config form  */}
                {
                    subscriptionDialog.channelType &&
                    <Form.Item
                        label="Channel config"
                    >
                        <Well>
                            <NotificationChannelConfigForm
                                prefix="channelConfig"
                                channelType={subscriptionDialog.channelType}
                            />
                        </Well>
                    </Form.Item>
                }
                {/*  TODO Content template  */}
            </FormDialog>
        </>
    )
}