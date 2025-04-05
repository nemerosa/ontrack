import {Button, Layout, Space} from "antd";
import Image from "next/image";

const {Content} = Layout;

export default function UserLoginPage() {
    return (
        <>
            <Layout style={{minHeight: "100vh", textAlign: "center"}}>
                <Content style={{padding: "300px 20px"}}>
                    <Space direction="vertical" size={50}>
                        <Image
                            src="/ontrack-128.png"
                            alt="Ontrack logo"
                            width={100}
                            height={100}
                        />
                        <a href="/api/auth/login" passHref>
                            <Button
                                type="primary"
                                size="large"
                                block
                                style={{
                                    padding: 30
                                }}
                            >
                                Login
                            </Button>
                        </a>
                    </Space>
                </Content>
            </Layout>
        </>
    )
}