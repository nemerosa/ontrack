import {Tag, Tooltip} from "antd";

function IssueCountTag({count, color, title}) {
    return (
        <>
            {
                typeof count === 'number' &&
                <Tooltip title={title}>
                    <Tag color={color}>
                        {count}
                    </Tag>
                </Tooltip>
            }
        </>
    )
}

export default function CHMLValidationDataType({levels}) {
    return (
        <>
            <IssueCountTag count={levels.CRITICAL} color="error" title="# of critical issues"/>
            <IssueCountTag count={levels.HIGH} color="warning" title="# of high severity issues"/>
            <IssueCountTag count={levels.MEDIUM} color="blue" title="# of medium severity issues"/>
            <IssueCountTag count={levels.LOW} color="default" title="# of low severity issues"/>
        </>
    )
}