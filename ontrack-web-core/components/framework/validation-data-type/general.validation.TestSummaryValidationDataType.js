import {Typography} from "antd";
import YesNo from "@components/common/YesNo";

export default function TestSummaryValidationDataType({warningIfSkipped, failWhenNoResults}) {
    return (
        <>
            <Typography.Paragraph>
                Warning if any skipped test: <YesNo value={warningIfSkipped} strong/>
            </Typography.Paragraph>
            <Typography.Paragraph>
                Failure if no test: <YesNo value={failWhenNoResults} strong/>
            </Typography.Paragraph>
        </>
    )
}