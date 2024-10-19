import {Table} from "antd";
import YesNo from "@components/common/YesNo";

const {Column} = Table

export default function SlotPipelineDeploymentStatusChecks({checks}) {
    return (
        <>
            <Table
                dataSource={checks}
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