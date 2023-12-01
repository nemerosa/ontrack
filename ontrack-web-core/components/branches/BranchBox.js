import {Space, Typography} from "antd";
import BranchFavourite from "@components/branches/BranchFavourite";
import BranchLink from "@components/branches/BranchLink";
import ProjectLink from "@components/projects/ProjectLink";

export default function BranchBox({branch, showProject, displayFavourite = true}) {
    return (
        <>
            <Space>
                {displayFavourite ? <BranchFavourite branch={branch}/> : undefined}
                {
                    showProject && <ProjectLink project={branch.project}/>
                }
                {
                    showProject && "/"
                }
                <BranchLink
                    branch={branch}
                    text={
                        <Typography.Text strong>{branch.name}</Typography.Text>
                    }
                />
            </Space>
        </>
    )
}