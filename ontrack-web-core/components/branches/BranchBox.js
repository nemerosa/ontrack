import {Card, Space} from "antd";
import {branchLink, projectLink} from "@components/common/Links";
import Favourite from "@components/common/Favourite";

const {Meta} = Card;

export default function BranchBox({branch, showProject, displayFavourite = true}) {
    return (
        <>
            <Space>
                { displayFavourite ? <Favourite value={branch.favourite}/> : undefined}
                {
                    showProject && projectLink(branch.project)
                }
                {
                    showProject && "/"
                }
                {branchLink(branch)}
            </Space>
        </>
    )
}