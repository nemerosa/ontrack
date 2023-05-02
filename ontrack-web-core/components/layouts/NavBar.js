import {Image, Space} from "antd";
import Link from "next/link";

export default function NavBar() {
    return (
        <>
            <Space direction="horizontal" size={16}>
                {/* Logo */}
                <Link href="/">
                    <Image width={32} src="/ontrack-128.png" preview={false}/>
                </Link>
                {/* TODO Title + link to home page */}
                {/* TODO Search component */}
                {/* TODO Application messages */}
                {/* TODO User name */}
                {/* TODO User menu (drawer) */}
            </Space>
        </>
    )
}