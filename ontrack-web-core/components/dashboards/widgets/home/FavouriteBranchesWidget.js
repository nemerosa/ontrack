import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {Space} from "antd";
import {useState} from "react";
import BranchBox from "@components/branches/BranchBox";
import {gql} from "graphql-request";
import FavouriteBranchesWidgetForm from "@components/dashboards/widgets/home/FavouriteBranchesWidgetForm";

export default function FavouriteBranchesWidget({project}) {

    const [branches, setBranches] = useState([])

    return (
        <>
            <SimpleWidget
                title={
                    project ? `Favourite branches for ${project}` : "Favourite branches"
                }
                query={
                    gql`
                        query FavouriteBranches($project: String) {
                            branches(favourite: true, project: $project) {
                                id
                                name
                            }
                        }
                    `
                }
                queryDeps={[project]}
                variables={{project: project}}
                setData={data => setBranches(data.branches)}
                form={<FavouriteBranchesWidgetForm project={project}/>}
            >
                <Space direction="horizontal" size={16} wrap>
                    {branches.map(branch => <BranchBox key={branch.id} branch={branch}/>)}
                </Space>
            </SimpleWidget>
        </>
    )
}