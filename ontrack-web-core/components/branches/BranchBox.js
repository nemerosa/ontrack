import {Space, Typography} from "antd";
import {branchLink, projectLink} from "@components/common/Links";
import BranchFavourite from "@components/branches/BranchFavourite";

export default function BranchBox({branch, showProject, displayFavourite = true}) {
    return (
        <>
            <Space>
                { displayFavourite ? <BranchFavourite branch={branch}/> : undefined}
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