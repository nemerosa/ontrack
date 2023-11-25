import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Alert, Button, Col, Row, Skeleton, Space, Tabs, Typography} from "antd";
import LayoutContextProvider from "@components/dashboards/layouts/LayoutContext";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import {gql} from "graphql-request";
import SelectableWidget from "@components/dashboards/widgets/SelectableWidget";
import LayoutSelector from "@components/dashboards/layouts/LayoutSelector";
import WidgetExpansionContextProvider from "@components/dashboards/layouts/WidgetExpansionContext";
import LayoutContainer from "@components/dashboards/layouts/LayoutContainer";
import {FaCog, FaWindowRestore} from "react-icons/fa";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function Dashboard() {

    const client = useGraphQLClient()

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
        if (client) {
            client.request(gql`
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
        }
    }, [client])

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
                                {!selectedDashboard.editionMode &&
                                    <WidgetExpansionContextProvider>
                                        <LayoutContainer loadedLayout={loadedLayout}/>
                                    </WidgetExpansionContextProvider>}
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
                                                <Tabs
                                                    items={[
                                                        {
                                                            key: 'widgets',
                                                            label: <Space>
                                                                <FaCog/>
                                                                <Typography.Text>Widgets</Typography.Text>
                                                            </Space>,
                                                            children: <Row wrap gutter={[16, 16]}>
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
                                                            </Row>,
                                                        },
                                                        {
                                                            key: 'layouts',
                                                            label: <Space>
                                                                <FaWindowRestore/>
                                                                <Typography.Text>Layouts</Typography.Text>
                                                            </Space>,
                                                            children: <LayoutSelector
                                                                selectedLayoutKey={selectedDashboard.layoutKey}
                                                                onLayoutKeySelected={onLayoutKeySelected}
                                                            />,
                                                        }
                                                    ]}
                                                />
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