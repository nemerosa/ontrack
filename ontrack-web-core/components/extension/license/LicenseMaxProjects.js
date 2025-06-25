import {Space, Typography} from "antd";
import ProjectCount from "@components/projects/ProjectCount";

export default function LicenseMaxProjects({maxProjects}) {
    return (
        <>
            <Space>
                {
                    maxProjects === 0 && <Typography.Text>No project limit</Typography.Text>
                }
                {
                    maxProjects > 0 && <Typography.Text>Max. {maxProjects} projects</Typography.Text>
                }
                (currently using <ProjectCount/> projects)
            </Space>
        </>
    )
}