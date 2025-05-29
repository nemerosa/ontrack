import {Layout, Space, theme} from "antd";
import MainPageBar from "@components/layouts/MainPageBar";
import {useContext} from "react";
import {MainLayoutContext} from "@components/layouts/MainLayout";
import MainGlobalMessages from "@components/layouts/MainGlobalMessages";

const {Content} = Layout;

export default function MainPage({title, breadcrumbs, commands, description, warning, children}) {

    const {
        token: {colorBgContainer},
    } = theme.useToken();

    const {expanded} = useContext(MainLayoutContext)

    return (
        <>
            <Layout>
                <Content
                    style={{
                        padding: 12,
                        paddingTop: 8,
                        minHeight: 280,
                        background: colorBgContainer,
                    }}
                >
                    {
                        !expanded && <MainPageBar
                            breadcrumbs={breadcrumbs}
                            title={title}
                            commands={commands}
                            description={description}
                        />
                    }
                    <Space direction="vertical" className="ot-line">
                        {warning}
                        <MainGlobalMessages/>
                        {children}
                    </Space>
                </Content>
            </Layout>
        </>
    )
}