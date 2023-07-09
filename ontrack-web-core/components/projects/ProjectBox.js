import {Card} from "antd";
import Favourite from "@components/common/Favourite";
import {projectLink} from "@components/common/Links";

const {Meta} = Card;

export default function ProjectBox({project, displayFavourite = true}) {
    return (
        <>
            <Card>
                <Meta
                    avatar={
                        displayFavourite ? <Favourite value={project.favourite}/> : undefined
                    }
                    title={projectLink(project)}
                />
            </Card>
        </>
    )
}