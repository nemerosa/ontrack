import {Typography} from "antd";

export default function SubscriptionContentTemplate({template}) {
    return template ? <Typography.Text code>{template}</Typography.Text> : <Typography.Text type="secondary">Using default template</Typography.Text>
}