import Link from "next/link";
import {Space, Typography} from "antd";
import {FaCogs} from "react-icons/fa";

export default function QueueDispatchResult({result}) {
    return (
        <>
            <Link
                href={`/extension/queue/records?id=${result.id}`}
            >
                <Space size="small">
                    <FaCogs/>
                    <Typography.Text>Queue message {result.type} </Typography.Text>
                    <Typography.Text code>{result.id}</Typography.Text>
                </Space>
            </Link>
        </>
    )
}