import {Space, Tooltip} from "antd";
import ValidationRunStatusIcon from "@components/validationRuns/ValidationRunStatusIcon";

const CoreValidationRunStatus = ({status, text}) => {
    return (
        <>
            <Space size={8}>
                <ValidationRunStatusIcon statusID={status.statusID}/>
                { text || status.statusID.name }
            </Space>
        </>
    )
}

export default function ValidationRunStatus({status, tooltip = true, text}) {
    return (
        <>
            {
                tooltip && <Tooltip title={status.statusID.name}>
                    <CoreValidationRunStatus status={status} text={text}/>
                </Tooltip>
            }
            {
                !tooltip && <CoreValidationRunStatus status={status} text={text}/>
            }
        </>
    )
}