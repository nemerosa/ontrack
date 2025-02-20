import {useEffect, useState} from "react";
import StandardPage from "@components/layouts/StandardPage";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import {Alert, Card, Col, Descriptions, Row, Space, Table, Tag, Typography} from "antd";
import PageSection from "@components/common/PageSection";
import LicenseActive from "@components/extension/license/LicenseActive";
import LicenseValidUntil from "@components/extension/license/LicenseValidUntil";
import LicenseMaxProjects from "@components/extension/license/LicenseMaxProjects";

export default function LicenseInfoPage() {

    const client = useGraphQLClient()

    const [licenseInfo, setLicenseInfo] = useState({})
    const [licenseItems, setLicenseItems] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query LicenseInfo {
                        licenseInfo {
                            license {
                                type
                                name
                                assignee
                                active
                                validUntil
                                maxProjects
                                licensedFeatures {
                                    id
                                    name
                                    enabled
                                    data {
                                        name
                                        value
                                    }
                                }
                            }
                            licenseControl {
                                active
                            }
                        }
                    }
                `
            ).then(data => {
                const info = data.licenseInfo
                setLicenseInfo(info)
                if (info.license) {
                    setLicenseItems([
                        {
                            key: 'type',
                            label: "Source",
                            children: info.license.type,
                        },
                        {
                            key: 'name',
                            label: "Name",
                            children: info.license.name,
                        },
                        {
                            key: 'assignee',
                            label: "Assignee",
                            children: info.license.assignee,
                        },
                        {
                            key: 'active',
                            label: "Activation",
                            children: <LicenseActive active={info.license.active}/>
                        },
                        {
                            key: 'validUntil',
                            label: "Valid until",
                            children: <LicenseValidUntil validUntil={info.license.validUntil}/>,
                        },
                        {
                            key: 'maxProjects',
                            label: "Max. projects",
                            children: <LicenseMaxProjects maxProjects={info.license.maxProjects}/>,
                        },
                    ])
                } else {
                    setLicenseItems([
                        {
                            key: 'none',
                            children: <Typography.Text strong>No license.</Typography.Text>
                        }
                    ])
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client])

    return (
        <StandardPage
            pageTitle="License info">
            <LoadingContainer loading={loading}>
                <Space direction="vertical">
                    {
                        licenseInfo?.licenseControl && (
                            licenseInfo.licenseControl.active ?
                                <Alert
                                    type="success"
                                    message="License is active."
                                    showIcon
                                /> :
                                <Alert
                                    type="error"
                                    message="License is disabled."
                                    showIcon
                                />
                        )
                    }
                    <PageSection
                        title=""
                        padding={true}
                    >
                        <Space direction="vertical">
                            <Descriptions
                                items={licenseItems}
                                layout="vertical"
                            />
                            <Row gutter={[8, 8]}>
                                {
                                    licenseInfo?.license?.licensedFeatures.map(feature => (
                                        <Col key={feature.id} span={6}>
                                            <Card
                                                title={feature.name}
                                            >
                                                <Space direction="vertical" className="ot-line">
                                                    {
                                                        feature.enabled ?
                                                            <Tag color="success">Enabled</Tag> :
                                                            <Tag color="error">Disabled</Tag>
                                                    }
                                                    {
                                                        feature.data.length > 0 &&
                                                        <Table
                                                            dataSource={feature.data}
                                                            size="small"
                                                            pagination={false}
                                                        >
                                                            <Table.Column
                                                                key="name"
                                                                title="Name"
                                                                dataIndex="name"
                                                            />
                                                            <Table.Column
                                                                key="value"
                                                                title="Value"
                                                                dataIndex="value"
                                                            />
                                                        </Table>
                                                    }
                                                </Space>
                                            </Card>
                                        </Col>
                                    ))
                                }
                            </Row>
                        </Space>
                    </PageSection>
                </Space>
            </LoadingContainer>
        </StandardPage>
    )
}