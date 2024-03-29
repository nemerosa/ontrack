import {Popover, Space, Tooltip} from "antd";
import ValidationRunStatusIcon from "@components/validationRuns/ValidationRunStatusIcon";

const CoreValidationRunStatus = ({id, status, displayText = true, text, onClick}) => {
    return (
        <>
            <Space data-testid={id} size={8} className={onClick ? "ot-action" : undefined} onClick={onClick}>
                <ValidationRunStatusIcon statusID={status.statusID}/>
                {displayText && (text || status.statusID.name)}
            </Space>
        </>
    )
}

export default function ValidationRunStatus({
                                                id,
                                                status,
                                                tooltip = true,
                                                tooltipContent,
                                                displayText = true, text,
                                                onClick,
                                            }) {
    const core = <CoreValidationRunStatus
        id={id}
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