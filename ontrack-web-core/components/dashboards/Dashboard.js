import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Alert, Button, Col, Form, Row, Select, Skeleton, Space, Typography} from "antd";
import LayoutContextProvider from "@components/dashboards/layouts/LayoutContext";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function Dashboard() {

    const {selectedDashboard} = useContext(DashboardContext)
    const selectedDashboardDispatch = useContext(DashboardDispatchContext)

    const importLayout = layoutKey => lazy(() =>
        import(`./layouts/${layoutKey}Layout`)
    )

    const [loadedLayout, setLoadedLayout] = useState(undefined)

    useEffect(() => {
        if (selectedDashboard?.layoutKey) {
            const loadLayout = async () => {
                const Layout = await importLayout(selectedDashboard.layoutKey)
                setLoadedLayout(<Layout/>)
            }
            loadLayout().then(() => {
            })
        }
    }, [selectedDashboard])

    const onStopEdition = () => {
        selectedDashboardDispatch({type: 'stopEdition'})
    }

    const [layouts, setLayouts] = useState([])
    useEffect(() => {
        graphQLCall(gql`
            query DashboardLayouts {
                dashboardLayouts {
                    value: key
                    label: name
                    description
                }
            }
        `).then(data => {
            setLayouts(data.dashboardLayouts)
        })
    }, [])

    const [form] = Form.useForm()
    useEffect(() => {
        if (selectedDashboard) {
            form.setFieldValue("layoutKey", selectedDashboard.layoutKey)
        }
    }, [selectedDashboard])

    const onLayoutKeySelected = (key) => {
        selectedDashboardDispatch({
            type: 'changeLayout',
            layoutKey: key,
        })
    }

    return (
        <>
            {
                selectedDashboard && <Suspense fallback={<Skeleton active/>}>
                    <Space direction="vertical" style={{
                        width: '100%'
                    }}>
                        {
                            selectedDashboard.editionMode &&
                            <Alert
                                type="warning"
                                message="Dashboard in edition mode."
                                action={
                                    <Button size="small" danger onClick={onStopEdition}>Close edition</Button>
                                }
                            />
                        }
                        <div>
                            <LayoutContextProvider>
                                {!selectedDashboard.editionMode && loadedLayout}
                                {
                                    selectedDashboard.editionMode && <Row>
                                        <Col span={18}>
                                            {loadedLayout}
                                        </Col>
                                        <Col span={6} style={{
                                            padding: '16px'
                                        }}>
                                            <Form
                                                layout="vertical"
                                                form={form}
                                            >
                                                <Form.Item name="layoutKey" label="Layout">
                                                    <Select options={layouts} onChange={onLayoutKeySelected}/>
                                                </Form.Item>
                                            </Form>
                                        </Col>
                                    </Row>
                                }
                            </LayoutContextProvider>
                        </div>
                    </Space>
                </Suspense>
            }
        </>
    )

}