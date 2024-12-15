import {Space, Tag, Typography} from "antd";
import Link from "next/link";
import {FaSpinner} from "react-icons/fa";

export default function NotificationStatusBadge({status, spin, count, href, title, showText = false}) {
    return (
        <>
            {
                count > 0 &&
                <Space>
                    <Tag
                        color={status}
                        title={showText ? undefined : title}
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
                    {
                        showText &&
                        <Typography.Text>{title}</Typography.Text>
                    }
                </Space>
            }
        </>
    )
}