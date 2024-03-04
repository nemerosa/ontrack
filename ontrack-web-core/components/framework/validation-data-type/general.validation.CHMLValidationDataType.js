import {Tag} from "antd";

// {"warningLevel":{"level":"HIGH","value":1},"failedLevel":{"level":"CRITICAL","value":1}}
export default function CHMLValidationDataType({warningLevel, failedLevel}) {
    return (
        <>
            {
                warningLevel &&
                <Tag>
                    Warning if {warningLevel.level} &ge; {warningLevel.value}
                </Tag>
            }
            {
                failedLevel &&
                <Tag>
                    Failed if {failedLevel.level} &ge; {failedLevel.value}
                </Tag>
            }
        </>
    )
}