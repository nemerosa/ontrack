import {Layout, Space, theme} from "antd";
import MainPageBar from "@components/layouts/MainPageBar";
import {useContext} from "react";
import {MainLayoutContext} from "@components/layouts/MainLayout";
import MainWarning from "@components/layouts/MainWarning";
import MainGlobalMessages from "@components/layouts/MainGlobalMessages";

const {Content} = Layout;

export default function MainPage({title, breadcrumbs, commands, description, children}) {

    const {
        token: {colorBgContainer},
    } = theme.useToken();

    const {expanded} = useContext(MainLayoutContext)

    return (
        <>
            <Layout>
                <Content
                    style={{
                        marginLeft: 8,
                        marginTop: 8,
                        marginRight: 8,
                        marginBottom: 0,
                        padding: 24,
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
                        <MainWarning/>
                        <MainGlobalMessages/>
                        {children}
                    </Space>
                </Content>
            </Layout>
        </>
    )
}