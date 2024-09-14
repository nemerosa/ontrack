import {Space, Tag, Typography} from "antd";
import Link from "next/link";
import {FaSpinner} from "react-icons/fa";

export default function NotificationStatusBadge({status, spin, count, href, title}) {
    return (
        <>
            {
                count > 0 &&
                <Tag
                    color={status}
                    title={title}
                >
                    <Link href={href}>
                        <Space>
                            {
                                spin && <FaSpinner className="anticon-spin"/>
                            }
                            <Typography.Text color={status}>{count}</Typography.Text>
                        </Space>
                    </Link>
                </Tag>
            }
        </>
    )
}