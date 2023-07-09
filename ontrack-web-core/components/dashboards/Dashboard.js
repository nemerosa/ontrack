import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Alert, Button, Card, Col, Collapse, Form, Row, Select, Skeleton, Space, Tooltip, Typography} from "antd";
import LayoutContextProvider from "@components/dashboards/layouts/LayoutContext";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import SelectableWidget from "@components/dashboards/widgets/SelectableWidget";
import LayoutSelector from "@components/dashboards/layouts/LayoutSelector";

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

    const onSaveEdition = () => {
        selectedDashboardDispatch({type: 'saveEdition'})
    }

    const [availableWidgets, setAvailableWidgets] = useState([])
    useEffect(() => {
        graphQLCall(gql`
            query DashboardWidgets {
                dashboardWidgets {
                    key
                    name
                    description
                    defaultConfig
                }
            }
        `).then(data => {
            setAvailableWidgets(data.dashboardWidgets)
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

    const addWidget = (widgetDef) => {
        return () => {
            selectedDashboardDispatch({
                type: 'addWidget',
                widgetDef: widgetDef,
            })
        }
    }

    return (
        <>
            {
                selectedDashboard && <Suspense fallback={<Skeleton active/>}>
                    <Space direction="vertical" style={{
                        width: '100%'
                    }}>
                        <div>
                            <LayoutContextProvider>
                                {!selectedDashboard.editionMode && loadedLayout}
                                {
                                    selectedDashboard.editionMode &&
                                    <Row>
                                        <Col span={18}>
                                            {loadedLayout}
                                        </Col>
                                        <Col span={6} style={{
                                            paddingLeft: '8px',
                                            paddingRight: '8px'
                                        }}>
                                            <Space direction="vertical">
                                                <Alert
                                                    type="warning"
                                                    message="Dashboard in edition mode."
                                                    action={
                                                        <Space>
                                                            <Button size="small" type="primary" onClick={onSaveEdition}>Save
                                                                dashboard</Button>
                                                            <Button size="small" danger onClick={onStopEdition}>Cancel
                                                                changes</Button>
                                                        </Space>
                                                    }
                                                />
                                                <Collapse defaultActiveKey={["widgets"]} accordion={true}>
                                                    <Collapse.Panel key="layout" header="Layout">
                                                        <LayoutSelector
                                                            selectedLayoutKey={selectedDashboard.layoutKey}
                                                            onLayoutKeySelected={onLayoutKeySelected}
                                                        />
                                                    </Collapse.Panel>
                                                    <Collapse.Panel key="widgets" header="Widgets">
                                                        <Row wrap gutter={[16, 16]}>
                                                            {
                                                                availableWidgets.map(availableWidget =>
                                                                    <Col span={24} key={availableWidget.key}>
                                                                        <SelectableWidget
                                                                            widgetDef={availableWidget}
                                                                            addWidget={addWidget}
                                                                        />
                                                                    </Col>
                                                                )
                                                            }
                                                        </Row>
                                                    </Collapse.Panel>
                                                </Collapse>
                                            </Space>
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