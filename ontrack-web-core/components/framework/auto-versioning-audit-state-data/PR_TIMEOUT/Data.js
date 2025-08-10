import Link from "next/link";
import {Space} from "antd";

export default function PrMergedData({order, data}) {
    const prLink = data.prLink
    const prName = data.prName
    if (prLink && prName) {
        return <Space>
            PR
            <Link href={prLink}>{prName}</Link>
            has been created but there was a timeout waiting for it to be mergeable.
        </Space>
    } else {
        return "No link to the PR but it has been created. There was a timeout waiting for it to be mergeable."
    }
}
