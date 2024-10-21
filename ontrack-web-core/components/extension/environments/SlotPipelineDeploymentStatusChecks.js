import {Table, Typography} from "antd";
import YesNo from "@components/common/YesNo";

const {Column} = Table

export default function SlotPipelineDeploymentStatusChecks({checks}) {
    return (
        <>
            <Table
                dataSource={checks}
                pagination={false}
                style={{width: '100%'}}
                title={() => <Typography.Title level={4}>Deployment checks</Typography.Title>}
            >

                <Column
                    key="status"
                    title="Deployable"
                    render={(_, item) => <YesNo value={item.check.status}/>}
                />

                <Column
                    key="override"
                    title="Overridden"
                    render={(_, item) => <YesNo value={item.check.override}/>}
                />

            </Table>
        </>
    )
}