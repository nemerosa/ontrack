import {Popover, Space, Tooltip} from "antd";
import ValidationRunStatusIcon from "@components/validationRuns/ValidationRunStatusIcon";

const CoreValidationRunStatus = ({status, displayText = true, text, onClick}) => {
    return (
        <>
            <Space size={8} className="ot-action" onClick={onClick}>
                <ValidationRunStatusIcon statusID={status.statusID}/>
                {displayText && (text || status.statusID.name)}
            </Space>
        </>
    )
}

export default function ValidationRunStatus({
                                                status,
                                                tooltip = true,
                                                tooltipContent,
                                                displayText = true, text,
                                                onClick,
                                            }) {
    const core = <CoreValidationRunStatus
        status={status}
        displayText={displayText}
        text={text}
        onClick={onClick}
    />
    return (
        <>
            {
                tooltip && <Popover
                    title={status.statusID.name}
                    content={tooltipContent}
                    placement="bottom"
                >
                    <div>
                        {core}
                    </div>
                </Popover>
            }
            {
                !tooltip && core
            }
        </>
    )
}