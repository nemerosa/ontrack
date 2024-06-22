import {Card, Tag} from "antd";
import SubscriptionLink from "@components/extension/notifications/SubscriptionLink";
import EventList from "@components/core/model/EventList";
import NotificationChannelConfig from "@components/extension/notifications/NotificationChannelConfig";
import SubscriptionContentTemplate from "@components/extension/notifications/SubscriptionContentTemplate";

export default function SubscriptionCard({subscription, entity, actions}) {
    return (
        <>
            <Card
                title={<SubscriptionLink subscription={subscription} entity={entity}/>}
                extra={actions}
            >
                <Card.Grid style={{width: '100%'}} hoverable={false}>
                    <EventList events={subscription.events}/>
                </Card.Grid>
                {
                    subscription.keywords &&
                    <Card.Grid style={{width: '100%'}} hoverable={false}>
                        Keywords: <Tag>{subscription.keywords}</Tag>
                    </Card.Grid>
                }
                <Card.Grid style={{width: '100%'}} hoverable={false}>
                    <Tag color="blue">{subscription.channel}</Tag>
                    <NotificationChannelConfig channel={subscription.channel} config={subscription.channelConfig}/>
                </Card.Grid>
                {
                    subscription.contentTemplate &&
                    <Card.Grid style={{width: '100%'}} hoverable={false}>
                        <p>Custom template:</p>
                        <SubscriptionContentTemplate template={subscription.contentTemplate}/>
                    </Card.Grid>
                }
            </Card>
        </>
    )
}