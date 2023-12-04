import {useContext, useEffect, useState} from "react";
import {DashboardContext} from "@components/dashboards/DashboardContextProvider";
import GridLayout from "@components/grid/GridLayout";
import DashboardWidgetCell from "@components/dashboards/DashboardWidgetCell";
import {Alert, Button, Col, Row, Space} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import SelectableWidget from "@components/dashboards/SelectableWidget";
import {v4} from "uuid";

export default function DashboardView() {

    const rowHeight = 50
    const defaultNewHeight = 4

    const client = useGraphQLClient()

    const {
        dashboard,
        recordLayout,
        expandedWidget,
        edition,
        saving,
        saveEdition,
        cancelEdition,
        addWidget,
    } = useContext(DashboardContext)

    const [layout, setLayout] = useState([])
    const [items, setItems] = useState({})

    const computeLayout = () => {
        return dashboard.widgets.map(widget => ({
            i: widget.uuid,
            ...widget.layout,
        }))
    }

    const maxYH = () => {
        let maxYH = 0
        layout.forEach(it => {
            maxYH = Math.max(maxYH, it.y + it.h)
        })
        return maxYH
    }

    const computeNewLayoutPosition = () => {
        return {
            x: 0,
            y: maxYH() + 1,
            w: 12,
            h: defaultNewHeight,
        }
    }

    const buildWidgetItems = (widgets) => widgets.reduce(
        (map, widget) => {
            map[widget.uuid] = <DashboardWidgetCell key={widget.uuid} widget={widget}/>
            return map
        }
        , {}
    )

    useEffect(() => {
        if (dashboard) {
            // console.log("[dashboard] Recomputing layout and items", dashboard)
            const computedLayout = computeLayout()
            // console.log("[dashboard] Recomputed layout", computedLayout)
            setLayout(computedLayout)
            setItems(buildWidgetItems(dashboard.widgets))
        }
    }, [dashboard]);

    const changeLayout = (newLayout) => {
        /**
         * Both the actual layout and the underlying widgets must be adapted
         */
        setLayout(newLayout)
        recordLayout(newLayout)
    }

    useEffect(() => {
        if (dashboard) {
            if (expandedWidget) {
                const widget = dashboard.widgets.find(it => it.uuid === expandedWidget)
                if (widget) {
                    setLayout([{
                        i: widget.uuid,
                        x: 0,
                        y: 0,
                        w: 12,
                        h: Math.max(maxYH(), defaultNewHeight * 2),
                    }])
                    setItems(buildWidgetItems([widget]))
                } else {
                    setLayout(computeLayout())
                    setItems(buildWidgetItems(dashboard.widgets))
                }
            } else {
                // No expansion any longer, restoring initial layout
                setLayout(computeLayout())
                setItems(buildWidgetItems(dashboard.widgets))
            }
        }
    }, [dashboard, expandedWidget]);

    const [availableWidgets, setAvailableWidgets] = useState([])
    useEffect(() => {
        if (client) {
            client.request(
                gql`
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

    const onSave = () => {
        saveEdition(layout)
    }

    const onStopEdition = () => {
        cancelEdition()
    }

    const onAddWidget = (widgetDef) => {
        // New widget
        const uuid = `new-${v4()}`;
        const widget = {
            uuid: uuid,
            key: widgetDef.key,
            config: widgetDef.defaultConfig,
            layout: computeNewLayoutPosition(),
        }
        // Adding the widget to the current dashboard
        addWidget(widget)
    }

    return (
        <>
            {
                dashboard && !edition && layout && items &&
                <GridLayout
                    layout={layout}
                    items={items}
                    rowHeight={rowHeight}
                />
            }
            {
                dashboard && edition &&
                <Row>
                    <Col span={18}>
                        <GridLayout
                            layout={layout}
                            items={items}
                            rowHeight={rowHeight}
                            setLayout={changeLayout}
                            isDraggable={true}
                            isResizable={true}
                        />
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
                                        <Button
                                            size="small"
                                            type="primary"
                                            onClick={onSave}
                                            disabled={
                                                saving || !dashboard?.widgets?.length
                                            }
                                        >
                                            Save dashboard
                                        </Button>
                                        <Button size="small" danger onClick={onStopEdition}>
                                            Cancel changes
                                        </Button>
                                    </Space>
                                }
                            />
                            <Row wrap gutter={[16, 16]}>
                                {
                                    availableWidgets.map(availableWidget =>
                                        <Col span={24} key={availableWidget.key}>
                                            <SelectableWidget
                                                widgetDef={availableWidget}
                                                addWidget={onAddWidget}
                                            />
                                        </Col>
                                    )
                                }
                            </Row>
                        </Space>
                    </Col>
                </Row>
            }
        </>
    )
}