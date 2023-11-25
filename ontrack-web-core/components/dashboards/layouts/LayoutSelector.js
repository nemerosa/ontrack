import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Col, Row} from "antd";
import SelectableLayout from "@components/dashboards/layouts/SelectableLayout";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function LayoutSelector({selectedLayoutKey, onLayoutKeySelected}) {

    const client = useGraphQLClient()

    const [layouts, setLayouts] = useState([])

    useEffect(() => {
        if (client) {
            client.request(gql`
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
        }
    }, [client])

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