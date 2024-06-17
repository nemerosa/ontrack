import {Empty, Space, Typography} from "antd";
import RowTag from "@components/common/RowTag";
import ProjectBox from "@components/projects/ProjectBox";

export default function SimpleProjectList({projects, emptyText}) {
    return (
        <>
            {
                projects && projects.length > 0 &&
                <Space direction="horizontal" size={16} wrap>
                    {
                        projects.map(project => <RowTag key={project.id}>
                                <ProjectBox project={project}/>
                            </RowTag>
                        )
                    }
                </Space>
            }
            {
                (!projects || projects.length === 0) && <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description={<Typography.Text>{emptyText}</Typography.Text>}
                />
            }
        </>
    )
}