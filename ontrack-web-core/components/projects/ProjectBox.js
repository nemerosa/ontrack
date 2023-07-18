import {Space, Typography} from "antd";
import Favourite from "@components/common/Favourite";
import {projectLink} from "@components/common/Links";
import Decorations from "@components/framework/decorations/Decorations";

export default function ProjectBox({project, displayFavourite = true, displayDecorations = true}) {
    return (
        <>
            <Space>
                {displayFavourite ? <Favourite value={project.favourite}/> : undefined}
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