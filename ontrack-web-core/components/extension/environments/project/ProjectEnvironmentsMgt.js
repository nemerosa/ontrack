import {Card, Col, Row, Space, Splitter, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import {
    useProjectEnvironmentsContext
} from "@components/extension/environments/project/ProjectEnvironmentsContextProvider";
import ProjectEnvironmentsBuilds from "@components/extension/environments/project/ProjectEnvironmentsBuilds";
import ProjectSlotGraph from "@components/extension/environments/project/ProjectSlotGraph";
import ProjectEnvironmentActions from "@components/extension/environments/project/ProjectEnvironmentActions";

export default function ProjectEnvironmentsMgt() {

    const {project, qualifier} = useProjectEnvironmentsContext()

    return (
        <>
            <Space direction="vertical" className="ot-line" size={16}>
                <Row gutter={16}>
                    <Col span={24}>
                        <Card
                            style={{height: "100%"}}
                        >
                            <Space>
                                <Typography.Text>Project</Typography.Text>
                                <ProjectLink project={project}/>
                                {
                                    qualifier &&
                                    <>
                                        <Typography.Text>[{qualifier}]</Typography.Text>
                                    </>
                                }
                            </Space>
                        </Card>
                    </Col>
                </Row>
                <Splitter>
                    <Splitter.Panel style={{paddingRight: '0.5em'}} defaultSize={20}>
                        <ProjectEnvironmentsBuilds/>
                    </Splitter.Panel>
                    <Splitter.Panel style={{paddingLeft: '0.5em', paddingRight: '0.5em'}} defaultSize={60}>
                        <Card
                            style={{height: "100%"}}
                            size="small"
                        >
                            <ProjectSlotGraph/>
                        </Card>
                    </Splitter.Panel>
                    <Splitter.Panel style={{paddingLeft: '0.5em'}} defaultSize={20}>
                        <ProjectEnvironmentActions/>
                    </Splitter.Panel>
                </Splitter>
            </Space>
        </>
    )
}