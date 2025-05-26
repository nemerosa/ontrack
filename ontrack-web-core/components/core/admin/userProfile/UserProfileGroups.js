import {useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";
import LoadingContainer from "@components/common/LoadingContainer";
import {Card, Col, List, Row} from "antd";

export default function UserProfileGroups() {
    const {data, loading} = useQuery(
        gql`
            query UserProfileGroups {
                user {
                    assignedGroups {
                        name
                    }
                    mappedGroups {
                        name
                    }
                    idpGroups
                }
            }
        `,
        {
            initialData: {
                assignedGroups: [],
                mappedGroups: [],
                idpGroups: [],
            },
            dataFn: data => data.user,
        }
    )

    return (
        <>
            <LoadingContainer loading={loading}>
                <Row gutter={16}>
                    <Col span={8}>
                        <Card size="small" title="Assigned groups" variant="borderless">
                            <List
                                itemLayout="horizontal"
                                dataSource={data.assignedGroups}
                                renderItem={(item) => item.name}
                            />
                        </Card>
                    </Col>
                    <Col span={8}>
                        <Card size="small" title="Mapped groups" variant="borderless">
                            <List
                                itemLayout="horizontal"
                                dataSource={data.mappedGroups}
                                renderItem={(item) => item.name}
                            />
                        </Card>
                    </Col>
                    <Col span={8}>
                        <Card size="small" title="IdP groups" variant="borderless">
                            <List
                                itemLayout="horizontal"
                                dataSource={data.idpGroups}
                                renderItem={item => item}
                            />
                        </Card>
                    </Col>
                </Row>
            </LoadingContainer>
        </>
    )
}