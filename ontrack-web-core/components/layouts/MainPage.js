import {Layout, theme} from "antd";
import MainPageBar from "@components/layouts/MainPageBar";

const {Content} = Layout;

export default function MainPage({title, breadcrumbs, commands, children}) {

    const {
        token: {colorBgContainer},
    } = theme.useToken();

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
                    <MainPageBar
                        breadcrumbs={breadcrumbs}
                        title={title}
                        commands={commands}
                    />
                    {children}
                </Content>
            </Layout>
        </>
    )
}