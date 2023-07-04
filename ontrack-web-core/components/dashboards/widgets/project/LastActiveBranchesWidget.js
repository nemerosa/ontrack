import {gql} from "graphql-request";
import {Space} from "antd";
import {useState} from "react";
import BranchBox from "@components/branches/BranchBox";
import SimpleWidget from "@components/dashboards/widgets/SimpleWidget";
import {checkContextIs} from "@components/dashboards/widgets/Widget";

export default function LastActiveBranchesWidget({count, context, contextId, editionMode}) {

    const [branches, setBranches] = useState([])

    const check = checkContextIs('Last active branches', context, 'project')
    if (check) return check

    return (
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
                projectId: contextId,
            }}
            setData={data => {
                setBranches(data.projects[0].branches)
            }}
            editionMode={editionMode}
        >
            <Space direction="horizontal" size={16} wrap>
                {branches.map(branch => <BranchBox key={branch.id} branch={branch}/>)}
            </Space>
        </SimpleWidget>
    )
}