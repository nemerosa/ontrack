import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {Col, Row} from "antd";
import SelectableLayout from "@components/dashboards/layouts/SelectableLayout";

export default function LayoutSelector({selectedLayoutKey, onLayoutKeySelected}) {

    const [layouts, setLayouts] = useState([])

    useEffect(() => {
        graphQLCall(gql`
            query DashboardLayouts {
                dashboardLayouts {
                    key
                    name
                    description
                }
            }
        `).then(data => {
            setLayouts(data.dashboardLayouts)
        })
    }, [])

    return (
        <>
            <Row wrap gutter={[16, 16]}>
                {
                    layouts.map(layoutDef =>
                        <Col span={12} key={layoutDef.key}>
                            <SelectableLayout
                                layoutDef={layoutDef}
                                selected={layoutDef.key === selectedLayoutKey}
                                onLayoutKeySelected={onLayoutKeySelected}
                            />
                        </Col>
                    )
                }
            </Row>
        </>
    )
}