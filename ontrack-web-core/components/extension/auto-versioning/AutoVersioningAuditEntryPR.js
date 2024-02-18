import Link from "next/link";
import {Space} from "antd";

export default function AutoVersioningAuditEntryPR({entry}) {
    return (
        <>
            {
                entry.mostRecentState.data.prName && entry.mostRecentState.data.prLink &&
                <Link href={entry.mostRecentState.data.prLink}>
                    <Space size={4}>
                        {entry.mostRecentState.data.prName}
                    </Space>
                </Link>
            }
        </>
    )
}