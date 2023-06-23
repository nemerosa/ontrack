import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Widget from "@components/dashboards/widgets/Widget";
import {Space} from "antd";
import {useEffect, useState} from "react";
import BranchBox from "@components/branches/BranchBox";

export default function LastActiveBranchesWidget({count, context, contextId}) {

    // TODO Check the project context

    const [loading, setLoading] = useState(true)
    const [branches, setBranches] = useState([])

    useEffect(() => {
        if (count) {
            setLoading(true)
            graphQLCall(
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
                `,
                {
                    projectId: contextId,
                }
            ).then(data => {
                setBranches(data.projects[0].branches)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [count])

    return (
        <Widget title={`Last ${count} active branches`} loading={loading}>
            <Space direction="horizontal" size={16} wrap>
                {branches.map(branch => <BranchBox key={branch.id} branch={branch}/>)}
            </Space>
        </Widget>
    )
}