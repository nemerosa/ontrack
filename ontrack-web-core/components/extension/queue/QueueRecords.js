import {gql} from "graphql-request";
import StandardTable from "@components/common/table/StandardTable";
import {Form, Input, Typography} from "antd";
import TimestampText from "@components/common/TimestampText";
import Duration from "@components/common/Duration";
import QueueRecordDetails from "@components/extension/queue/QueueRecordDetails";
import SelectQueueProcessor from "@components/extension/queue/SelectQueueProcessor";
import SelectQueueRecordState from "@components/extension/queue/SelectQueueRecordState";
import QueuePurgeButton from "@components/extension/queue/QueuePurgeButton";
import {useReloadState} from "@components/common/StateUtils";

export default function QueueRecords() {

    const [reloadCount, reload] = useReloadState()

    return (
        <>
            <StandardTable
                filterForm={[
                    <Form.Item
                        key="id"
                        name="id"
                        label="ID"
                    >
                        <Input style={{width: "24em"}}/>
                    </Form.Item>,
                    <Form.Item
                        key="processor"
                        name="processor"
                        label="Processor"
                    >
                        <SelectQueueProcessor/>
                    </Form.Item>,
                    <Form.Item
                        key="state"
                        name="state"
                        label="State"
                    >
                        <SelectQueueRecordState/>
                    </Form.Item>,
                    <Form.Item
                        key="text"
                        name="text"
                        label="Text"
                    >
                        <Input style={{width: "24em"}}/>
                    </Form.Item>,
                    <Form.Item
                        key="routingKey"
                        name="routingKey"
                        label="Routing key"
                    >
                        <Input style={{width: "24em"}}/>
                    </Form.Item>,
                    <Form.Item
                        key="queueName"
                        name="queueName"
                        label="Queue name"
                    >
                        <Input style={{width: "24em"}}/>
                    </Form.Item>,
                    <Form.Item
                        key="username"
                        name="username"
                        label="Username"
                    >
                        <Input style={{width: "8em"}}/>
                    </Form.Item>,
                ]}
                filterExtraButtons={[
                    <QueuePurgeButton key="purge" onDone={reload}/>,
                ]}
                reloadCount={reloadCount}
                query={
                    gql`
                        query QueueRecords(
                            $offset: Int! = 0,
                            $size: Int! = 20,
                            $id: String,
                            $processor: String,
                            $state: QueueRecordState,
                            $text: String,
                            $routingKey: String,
                            $queueName: String,
                            $username: String,
                        ) {
                            queueRecordings(
                                offset: $offset,
                                size: $size,
                                filter: {
                                    id: $id,
                                    processor: $processor,
                                    state: $state,
                                    text: $text,
                                    routingKey: $routingKey,
                                    queueName: $queueName,
                                    username: $username,
                                },
                            ) {
                                pageInfo {
                                    nextPage {
                                        offset
                                        size
                                    }
                                }
                                pageItems {
                                    key: id
                                    id
                                    state
                                    queuePayload {
                                        processor
                                        body
                                    }
                                    startTime
                                    endTime
                                    durationSeconds
                                    routingKey
                                    queueName
                                    actualPayload
                                    exception
                                    history {
                                        state
                                        time
                                    }
                                    source {
                                        feature
                                        id
                                        data
                                    }
                                    username
                                }
                            }
                        }
                    `
                }
                queryNode="queueRecordings"
                filter={{}}
                columns={[
                    {
                        key: "id",
                        title: "ID",
                        render: (_, record) => <Typography.Text code>{record.id}</Typography.Text>
                    },
                    {
                        key: "processor",
                        title: "Processor",
                        render: (_, record) => <Typography.Text>{record.queuePayload.processor}</Typography.Text>
                    },
                    {
                        key: "state",
                        title: "State",
                        render: (_, record) => <Typography.Text code>{record.state}</Typography.Text>
                    },
                    {
                        key: "startTime",
                        title: "Start time",
                        render: (_, record) =>
                            <Typography.Text>
                                <TimestampText
                                    format="YYYY MMM DD, HH:mm:ss"
                                    value={record.startTime}
                                />
                            </Typography.Text>
                    },
                    {
                        key: "endTime",
                        title: "End time",
                        render: (_, record) =>
                            <Typography.Text>
                                <TimestampText
                                    format="YYYY MMM DD, HH:mm:ss"
                                    value={record.endTime}
                                />
                            </Typography.Text>
                    },
                    {
                        key: "duration",
                        title: "Duration",
                        render: (_, record) => <>
                            {
                                record.durationSeconds > 0 &&
                                <Typography.Text>
                                    <Duration seconds={record.durationSeconds}/>
                                </Typography.Text>
                            }
                            {
                                record.durationSeconds === 0 &&
                                <Typography.Text>
                                    &lt; 1 second
                                </Typography.Text>
                            }
                        </>
                    },
                ]}
                expandable={{
                    expandedRowRender: (record) => (
                        <QueueRecordDetails record={record}/>
                    ),
                }}
            />
        </>
    )

}