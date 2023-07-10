import {Space} from "antd";
import Favourite from "@components/common/Favourite";
import {projectLink} from "@components/common/Links";

export default function ProjectBox({project, displayFavourite = true}) {
    return (
        <>
            <Space>
                {displayFavourite ? <Favourite value={project.favourite}/> : undefined}
                {projectLink(project)}
            </Space>
        </>
    )
}