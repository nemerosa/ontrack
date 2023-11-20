import {Space, Typography} from "antd";
import {projectLink} from "@components/common/Links";
import Decorations from "@components/framework/decorations/Decorations";
import ProjectFavourite from "@components/projects/ProjectFavourite";

export default function ProjectBox({project, displayFavourite = true, displayDecorations = true}) {
    return (
        <>
            <Space>
                {displayFavourite ? <ProjectFavourite project={project}/> : undefined}
                {
                    projectLink(project, <Typography.Text strong>{project.name}</Typography.Text>)
                }
                {
                    displayDecorations && <Decorations entity={project}/>
                }
            </Space>
        </>
    )
}