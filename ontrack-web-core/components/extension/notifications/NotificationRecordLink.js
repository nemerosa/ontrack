import Link from "next/link";
import {FaEnvelope} from "react-icons/fa";
import {Space} from "antd";

export default function NotificationRecordLink({recordId, text}) {
    return (
        <>
            <Link
                href={`/extension/notifications/recordings/${recordId}`}
                title="Link to notification record"
            >
                <Space>
                    <FaEnvelope/>
                    {text}
                </Space>
            </Link>
        </>
    )
}