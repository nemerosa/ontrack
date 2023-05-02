import {Image, Space, Typography} from "antd";
import Link from "next/link";

const {Text} = Typography;

export default function NavBar() {
    return (
        <>
            <Space direction="horizontal" size={16}>
                {/* Logo */}
                <Link href="/">
                    <Image width={32} src="/ontrack-128.png" preview={false}/>
                </Link>
                {/* Title + link to home page */}
                <Link href="/">
                    <Text style={{color: "white", fontSize: '175%', verticalAlign: 'middle'}}>Ontrack</Text>
                </Link>
                {/* TODO Search component */}
                {/* TODO Application messages */}
                {/* TODO User name */}
                {/* TODO User menu (drawer) */}
            </Space>
        </>
    )
}