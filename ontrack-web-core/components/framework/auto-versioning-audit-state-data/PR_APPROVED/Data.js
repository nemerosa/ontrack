import Link from "next/link";
import {Space} from "antd";

export default function PrMergedData({order, data}) {
    const prLink = data.prLink
    const prName = data.prName
    if (prLink && prName) {
        return <Space>
            PR
            <Link href={prLink}>{prName}</Link>
            has been approved.
        </Space>
    } else {
        return "No link to the PR but it has been approved."
    }
}
