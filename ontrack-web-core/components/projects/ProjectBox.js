import {Card} from "antd";
import Favourite from "@components/common/Favourite";
import {projectLink} from "@components/common/Links";

const {Meta} = Card;

export default function ProjectBox({project}) {
    return (
        <>
            <Card>
                <Meta
                    avatar={<Favourite value={project.favourite}/>}
                    title={projectLink(project)}
                />
            </Card>
        </>
    )
}