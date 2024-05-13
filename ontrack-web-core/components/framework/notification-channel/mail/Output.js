import {Descriptions} from "antd";
import JsonDisplay from "@components/common/JsonDisplay";

export default function MailNotificationChannelOutput({to, cc, subject, body}) {
    return (
        <>
            <Descriptions
                column={12}
                items={[
                {
                    key: 'to',
                    label: 'To',
                    children: to,
                    span: 12,
                },
                {
                    key: 'cc',
                    label: 'Cc',
                    children: cc,
                    span: 12,
                },
                {
                    key: 'subject',
                    label: 'Subject',
                    children: subject,
                    span: 12,
                },
                {
                    key: 'body',
                    label: 'Body',
                    children: body,
                    span: 12,
                },
            ]}/>
        </>
    )
}