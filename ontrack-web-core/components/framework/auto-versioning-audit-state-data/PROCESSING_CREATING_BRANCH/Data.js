import {Typography} from "antd";
import CodeData from "@components/framework/auto-versioning-audit-state-data/support/CodeData";

export default function PrCreatingData({data}) {
    return <Typography.Text>
        Branch <CodeData text={data.branch}/> has been pushed.
    </Typography.Text>
}
