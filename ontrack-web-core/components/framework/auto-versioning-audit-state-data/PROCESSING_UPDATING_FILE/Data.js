import {Typography} from "antd";
import CodeData from "@components/framework/auto-versioning-audit-state-data/support/CodeData";

export default function PrCreatingData({data}) {
    return <Typography.Text>
        Updating file <CodeData text={data.path}/> on branch <CodeData text={data.branch}/>.
    </Typography.Text>
}
