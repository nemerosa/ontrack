import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {useState} from "react";
import {gql} from "graphql-request";
import FavouriteBranchesWidgetForm from "@components/dashboards/widgets/home/FavouriteBranchesWidgetForm";
import BranchList from "@components/branches/BranchList";
import {useDashboardEventForRefresh} from "@components/common/EventsContext";
import {Empty} from "antd";

export default function FavouriteBranchesWidget({project}) {

    const favouriteRefreshCount = useDashboardEventForRefresh("branch.favourite")
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
                queryDeps={[project, favouriteRefreshCount]}
                variables={{project: project}}
                setData={data => setBranches(data.branches)}
                form={<FavouriteBranchesWidgetForm project={project}/>}
            >
                <BranchList
                    branches={branches}
                    showProject={!project}
                />
                {
                    (!branches || branches.length === 0) && <Empty
                        image={Empty.PRESENTED_IMAGE_SIMPLE}
                        description="No branch has been marked as a favourite in any project."
                    />
                }
            </SimpleWidget>
        </>
    )
}