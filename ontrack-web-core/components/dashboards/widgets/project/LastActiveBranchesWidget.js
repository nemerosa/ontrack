import {gql} from "graphql-request";
import {Space} from "antd";
import {useContext, useState} from "react";
import BranchBox from "@components/branches/BranchBox";
import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {DashboardContext} from "@components/dashboards/DashboardContext";
import LastActiveBranchesWidgetForm from "@components/dashboards/widgets/project/LastActiveBranchesWidgetForm";

export default function LastActiveBranchesWidget({count}) {

    const dashboard = useContext(DashboardContext)
    const [branches, setBranches] = useState([])

    // const check = checkContextIs('Last active branches', 'project')
    // if (check) return check

    return (
        <>
            <SimpleWidget
                title={`Last ${count} active branches`}
                query={
                    gql`
                        query LastActiveBranches(
                            $projectId: Int!,
                            $count: Int! = 10,
                        ) {
                            projects(id: $projectId) {
                                branches(count: $count, order: true) {
                                    id
                                    name
                                }
                            }
                        }
                    `
                }
                variables={{
                    projectId: dashboard.contextId,
                }}
                setData={data => {
                    setBranches(data.projects[0].branches)
                }}
                form={<LastActiveBranchesWidgetForm count={count}/>}
            >
                <Space direction="horizontal" size={16} wrap>
                    {branches.map(branch => <BranchBox key={branch.id} branch={branch}/>)}
                </Space>
            </SimpleWidget>
        </>
    )
}