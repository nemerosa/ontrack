import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {useState} from "react";
import {gql} from "graphql-request";
import FavouriteBranchesWidgetForm from "@components/dashboards/widgets/home/FavouriteBranchesWidgetForm";
import {gqlDecorationFragment} from "@components/services/fragments";
import BranchList from "@components/branches/BranchList";

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
                              disabled
                              favourite
                              project {
                                id
                                name
                              }
                              latestBuild: builds(count: 1) {
                                id
                                name
                              }
                              promotionLevels {
                                id
                                name
                                image
                                promotionRuns(first: 1) {
                                  build {
                                    id
                                    name
                                  }
                                }
                              }
                            }
                        }
                    `
                }
                queryDeps={[project]}
                variables={{project: project}}
                setData={data => setBranches(data.branches)}
                form={<FavouriteBranchesWidgetForm project={project}/>}
            >
                <BranchList
                    branches={branches}
                    showProject={!project}
                />
            </SimpleWidget>
        </>
    )
}