import Link from "next/link";
import {Space} from "antd";
import {AutoVersioningAuditEntryPRStatus} from "@components/extension/auto-versioning/AutoVersioningAuditEntryPRStatus";

export default function AutoVersioningAuditEntryPR({entry, displayStatus}) {
    return (
        <>
            {
                entry.mostRecentState.data.prName && entry.mostRecentState.data.prLink &&
                <Space>
                    <Link href={entry.mostRecentState.data.prLink}>
                        <Space size={4}>
                            {entry.mostRecentState.data.prName}
                        </Space>
                    </Link>
                    {
                        displayStatus &&
                        entry.pullRequest?.status &&
                        <AutoVersioningAuditEntryPRStatus prName={entry.mostRecentState.data.prName} status={entry.pullRequest?.status}/>
                    }
                </Space>
            }
        </>
    )
}