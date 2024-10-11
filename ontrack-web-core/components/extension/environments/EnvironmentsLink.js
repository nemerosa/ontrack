import {Space, Typography} from "antd";
import {FaServer} from "react-icons/fa";
import Link from "next/link";

export const environmentsUri = '/extension/environments/environments'

export default function EnvironmentsLink() {
    return (
        <>
            <Link href={environmentsUri}>
                <Space>
                    <FaServer/>
                    <Typography.Text>Environments</Typography.Text>
                </Space>
            </Link>
        </>
    )
}