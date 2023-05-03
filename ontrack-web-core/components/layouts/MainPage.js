import {Breadcrumb, Layout, theme, Typography} from "antd";

const {Content} = Layout;
const {Text} = Typography;

export default function MainPage({title, breadcrumbs, children}) {

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
                    <Breadcrumb
                        style={{
                            margin: '16px 0',
                        }}
                    >
                        {
                            breadcrumbs ?
                                breadcrumbs.map((item, index) => {
                                    return <Breadcrumb.Item key={index}>{item}</Breadcrumb.Item>
                                }) :
                                undefined
                        }
                        {/*<Breadcrumb.Item>Home</Breadcrumb.Item>*/}
                        {/*<Breadcrumb.Item>List</Breadcrumb.Item>*/}
                        <Breadcrumb.Item><Text strong>{title}</Text></Breadcrumb.Item>
                    </Breadcrumb>
                    {children}
                </Content>
            </Layout>
        </>
    )
}