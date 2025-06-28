import {Typography} from "antd";
import CodeData from "@components/framework/auto-versioning-audit-state-data/support/CodeData";

export default function PrCreatingData({data}) {
    return <Typography.Text>
        Post-processing for branch <CodeData text={data.branch}/> is complete.
    </Typography.Text>
}
