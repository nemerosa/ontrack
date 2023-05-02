import {Layout} from "antd";
import NavBar from "@components/layouts/NavBar";

const {Content, Header} = Layout;

export default function MainLayout({children}) {
    return (
        <>
            <Layout>
                <Header>
                    <NavBar/>
                </Header>
                <Content>
                    {children}
                </Content>
            </Layout>
        </>
    )
}