import {Tag} from "antd";

const statuses = {
    OPEN: {
        name: "Open",
        color: "blue",
    },
    MERGED: {
        name: "Merged",
        color: "success",
    },
    DECLINED: {
        name: "Declined",
        color: "error",
    }
}

export function AutoVersioningAuditEntryPRStatus({prName, status}) {
    return (
        <Tag color={statuses[status]?.color}
             className="ot-pr-status"
             data-pr-name={prName}
        >
            {statuses[status]?.name}
        </Tag>
    )
}