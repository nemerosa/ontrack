import {Popover, Space} from "antd";
import ValidationRunStatusIcon from "@components/validationRuns/ValidationRunStatusIcon";
import Link from "next/link";

const CoreValidationRunStatus = ({id, status, displayText = true, text, onClick, href}) => {
    return (
        <>
            <Space data-testid={id} size={8} className={onClick ? "ot-action" : undefined} onClick={onClick}>
                {
                    href && <Link href={href}>
                        <ValidationRunStatusIcon statusID={status.statusID}/>
                    </Link>
                }
                {
                    !href && <ValidationRunStatusIcon statusID={status.statusID}/>
                }
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
                                                onClick, href,
                                            }) {
    const core = <CoreValidationRunStatus
        id={id}
        status={status}
        displayText={displayText}
        text={text}
        onClick={onClick}
        href={href}
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