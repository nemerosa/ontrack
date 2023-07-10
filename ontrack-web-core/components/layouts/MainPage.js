import {Layout, theme} from "antd";
import MainPageBar from "@components/layouts/MainPageBar";
import {useContext} from "react";
import {MainLayoutContext} from "@components/layouts/MainLayout";

const {Content} = Layout;

export default function MainPage({title, breadcrumbs, commands, children}) {

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
                        />
                    }
                    {children}
                </Content>
            </Layout>
        </>
    )
}