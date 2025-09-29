import StandardTable from "@components/common/table/StandardTable";
import {gql} from "graphql-request";
import TimestampText from "@components/common/TimestampText";
import WebhookExchangeTableDetails from "@components/extension/notifications/webhooks/WebhookExchangeTableDetails";

export default function WebhookExchangesTable({name}) {
    return (
        <>
            <StandardTable
                query={
                    gql`
                        query WebhookExchanges($name: String!) {
                            webhookByName(name: $name) {
                                exchanges {
                                    pageInfo {
                                        nextPage {
                                            offset
                                            size
                                        }
                                    }
                                    pageItems {
                                        uuid
                                        request {
                                            timestamp
                                            type
                                            payload
                                        }
                                        response {
                                            timestamp
                                            code
                                            payload
                                        }
                                    }
                                }
                            }
                        }
                    `
                }
                variables={{name}}
                filter={{}}
                queryNode={data => data.webhookByName?.exchanges}
                columns={[
                    {
                        key: "uuid",
                        title: "UUID",
                        dataIndex: "uuid",
                    },
                    {
                        key: "timestamp",
                        title: "Timestamp",
                        render: (_, record) => <TimestampText value={record.request?.timestamp}/>,
                    },
                    {
                        key: "type",
                        title: "Type",
                        render: (_, record) => record.request?.type,
                    },
                    {
                        key: "code",
                        title: "Code",
                        render: (_, record) => record.response?.code,
                    },
                ]}
                rowKey={record => record.uuid}
                expandable={{
                    expandedRowRender: (webhookExchange) => (
                        <WebhookExchangeTableDetails webhookExchange={webhookExchange}/>
                    )
                }}
            />
        </>
    )
}