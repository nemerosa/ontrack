import {Typography} from "antd";
import YesNo from "@components/common/YesNo";

export default function TestSummaryValidationDataType({warningIfSkipped}) {
    return <Typography.Text>
        Warning if any skipped test: <YesNo value={warningIfSkipped} strong/>
    </Typography.Text>
}