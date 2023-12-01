import {Space, Typography} from "antd";
import Decorations from "@components/framework/decorations/Decorations";
import ProjectFavourite from "@components/projects/ProjectFavourite";
import ProjectLink from "@components/projects/ProjectLink";

export default function ProjectBox({project, displayFavourite = true, displayDecorations = true}) {
    return (
        <>
            <Space>
                {displayFavourite ? <ProjectFavourite project={project}/> : undefined}
                <ProjectLink
                    project={project}
                    text={<Typography.Text strong>{project.name}</Typography.Text>}
                />
                {
                    displayDecorations && <Decorations entity={project}/>
                }
            </Space>
        </>
    )
}