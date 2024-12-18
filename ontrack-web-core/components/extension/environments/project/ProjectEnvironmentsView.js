import Head from "next/head";
import {projectTitle} from "@components/common/Titles";
import {gql} from "graphql-request";
import {useQuery} from "@components/services/useQuery";
import MainPage from "@components/layouts/MainPage";
import {downToProjectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {projectUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import ProjectSlotGraph from "@components/extension/environments/project/ProjectSlotGraph";
import {Card, Col, Row, Space, Typography} from "antd";
import ProjectSlotSelection from "@components/extension/environments/project/ProjectSlotSelection";
import ProjectLink from "@components/projects/ProjectLink";

export default function ProjectEnvironmentsView({id}) {

    const qualifier = "demo"

    const {loading, data} = useQuery(
        gql`
            query Project($id: Int!) {
                project(id: $id) {
                    id
                    name
                }
            }
        `,
        {
            variables: {id},
            initialData: {project: {}}
        }
    )

    return (
        <>
            <Head>
                {projectTitle(data?.project, "Environments")}
            </Head>
            <MainPage
                title="Environments"
                breadcrumbs={downToProjectBreadcrumbs(data)}
                commands={[
                    <CloseCommand key="close" href={projectUri(data?.project)}/>,
                ]}
            >
                <LoadingContainer loading={loading}>
                    <Space direction="vertical" className="ot-line" size={16}>
                        <Row gutter={16}>
                            <Col span={4}>
                                <Card
                                    style={{height: "100%"}}
                                >
                                    <Space>
                                        <Typography.Text>Project</Typography.Text>
                                        <ProjectLink project={data.project}/>
                                        {
                                            qualifier &&
                                            <>
                                                <Typography.Text>[{qualifier}]</Typography.Text>
                                            </>
                                        }
                                    </Space>
                                </Card>
                            </Col>
                            <Col span={20}>
                                <Card
                                    style={{height: "100%"}}
                                >
                                    TODO List of builds
                                </Card>
                            </Col>
                        </Row>
                        <Row gutter={16}>
                            <Col span={20}>
                                <ProjectSlotGraph id={id} qualifier=""/>
                            </Col>
                            <Col span={4}>
                                <ProjectSlotSelection/>
                            </Col>
                        </Row>
                    </Space>
                </LoadingContainer>
            </MainPage>
        </>
    )
}