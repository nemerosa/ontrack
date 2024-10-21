import {Space, Table, Typography} from "antd";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";
import YesNo from "@components/common/YesNo";

export default function SlotPipelineDeploymentChangeTable({changes}) {
    return (
        <>
            <Table
                dataSource={changes}
                pagination={false}
                style={{width: '100%'}}
                title={() => <Typography.Title level={4}>Changes</Typography.Title>}
            >
                <Table.Column
                    key="user"
                    title="User"
                    dataIndex="user"
                />
                <Table.Column
                    key="timestamp"
                    title="Timestamp"
                    dataIndex="timestamp"
                />
                <Table.Column
                    key="status"
                    title="Status"
                    render={(_, change) => <SlotPipelineStatus pipeline={change}/>}
                />
                <Table.Column
                    key="message"
                    title="Message"
                    dataIndex="message"
                />
                <Table.Column
                    key="override"
                    title="Overridden"
                    render={
                        (_, change) => <Space>
                            <YesNo value={change.override}/>
                            <Typography.Text>{change.overrideMessage}</Typography.Text>
                        </Space>
                    }
                />
            </Table>
        </>
    )
}