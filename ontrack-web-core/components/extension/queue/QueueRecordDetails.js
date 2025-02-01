import {Descriptions, Typography} from "antd";
import JsonDisplay from "@components/common/JsonDisplay";
import QueueRecordHistory from "@components/extension/queue/QueueRecordHistory";
import QueueRecordSource from "@components/extension/queue/QueueRecordSource";

export default function QueueRecordDetails({record}) {
    return (
        <>
            <Descriptions
                column={12}
                layout="vertical"
                size="small"
                items={[
                    {
                        key: 'routing',
                        label: "Routing",
                        span: 4,
                        children: <Typography.Text code>{record.routingKey}</Typography.Text>
                    },
                    {
                        key: 'queue',
                        label: "Queue",
                        span: 4,
                        children: <Typography.Text code>{record.queueName}</Typography.Text>
                    },
                    {
                        key: 'username',
                        label: "Username",
                        span: 4,
                        children: <Typography.Text code>{record.username}</Typography.Text>
                    },
                    {
                        key: 'queuePayload',
                        label: "Queue payload",
                        span: 12,
                        children: <JsonDisplay
                            value={JSON.stringify(record.queuePayload.body, null, 2)}
                            height="8em"
                        />,
                    },
                    {
                        key: 'actualPayload',
                        label: "Actual payload",
                        span: 12,
                        children: <JsonDisplay
                            value={JSON.stringify(record.actualPayload, null, 2)}
                            height="8em"
                        />,
                    },
                    {
                        key: 'history',
                        label: "History",
                        span: 6,
                        children: <QueueRecordHistory record={record}/>,
                    },
                    {
                        key: 'source',
                        label: "Source",
                        span: 6,
                        children: <QueueRecordSource record={record}/>,
                    },
                ]}
            />
        </>
    )
}