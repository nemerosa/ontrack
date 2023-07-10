import {Space, Typography} from "antd";
import {branchLink, projectLink} from "@components/common/Links";
import Favourite from "@components/common/Favourite";

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
                {
                    branchLink(branch, <Typography.Text strong>{branch.name}</Typography.Text>)
                }
            </Space>
        </>
    )
}