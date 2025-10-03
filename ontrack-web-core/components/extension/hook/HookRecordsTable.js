import StandardTable from "@components/common/table/StandardTable";
import {gql} from "graphql-request";
import {Form, Input, Typography} from "antd";
import TimestampText from "@components/common/TimestampText";
import {HookRecordDetails} from "@components/extension/hook/HookRecordDetails";
import SelectHook from "@components/extension/hook/SelectHook";
import SelectHookState from "@components/extension/hook/SelectHookState";

export default function HookRecordsTable() {
    return (
        <>
            <StandardTable
                query={
                    gql`
                        query HookRecords(
                            $id: String,
                            $hook: String,
                            $state: HookRecordState,
                            $text: String,
                        ) {
                            hookRecordings(
                                filter: {
                                    id: $id,
                                    hook: $hook,
                                    state: $state,
                                    text: $text,
                                }
                            ) {
                                pageInfo {
                                    nextPage {
                                        offset
                                        size
                                    }
                                }
                                pageItems {
                                    id
                                    hook
                                    state
                                    request {
                                        body
                                        parameters {
                                            name
                                            value
                                        }
                                    }
                                    startTime
                                    endTime
                                    message
                                    exception
                                    response {
                                        type
                                        infoLink {
                                            feature
                                            id
                                            data
                                        }
                                    }
                                }
                            }
                        }
                    `
                }
                queryNode="hookRecordings"
                variables={{}}
                filter={{}}
                columns={[
                    {
                        key: 'id',
                        title: 'ID',
                        dataIndex: 'id',
                        render: (value, _) => <Typography.Text code copyable>{value}</Typography.Text>,
                    },
                    {
                        key: 'hook',
                        title: 'Hook',
                        dataIndex: 'hook',
                        render: (value, _) => <Typography.Text code>{value}</Typography.Text>,
                    },
                    {
                        key: 'startTime',
                        title: 'Start time',
                        dataIndex: 'startTime',
                        render: (value, _) => <TimestampText value={value} format="YYYY MMM DD, HH:mm:ss"/>,
                    },
                    {
                        key: 'state',
                        title: 'State',
                        dataIndex: 'state',
                        render: (value, _) => <Typography.Text code>{value}</Typography.Text>,
                    },
                    {
                        key: 'endTime',
                        title: 'End time',
                        dataIndex: 'endTime',
                        render: (value, _) => <TimestampText value={value} format="YYYY MMM DD, HH:mm:ss"/>,
                    },
                ]}
                rowKey={record => record.id}
                expandable={{
                    expandedRowRender: (record) => (
                        <HookRecordDetails record={record}/>
                    )
                }}
                filterForm={[
                    <Form.Item
                        key="id"
                        name="id"
                        label="ID"
                    >
                        <Input style={{width: "24em"}}/>
                    </Form.Item>,
                    <Form.Item
                        key="hook"
                        name="hook"
                        label="Hook"
                    >
                        <SelectHook/>
                    </Form.Item>,
                    <Form.Item
                        key="state"
                        name="state"
                        label="State"
                    >
                        <SelectHookState/>
                    </Form.Item>,
                    <Form.Item
                        key="text"
                        name="text"
                        label="Text"
                    >
                        <Input style={{width: "24em"}}/>
                    </Form.Item>,
                ]}
            />
        </>
    )
}