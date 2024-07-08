import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import BranchLink from "@components/branches/BranchLink";
import {Space, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <>
                <Space>
                    <ProjectLink project={data.branch.project}/>
                    <Typography.Text type="secondary">/</Typography.Text>
                    <BranchLink branch={data.branch}/>
                </Space>
            </>
        }
        description={data.branch.description}
    />
}