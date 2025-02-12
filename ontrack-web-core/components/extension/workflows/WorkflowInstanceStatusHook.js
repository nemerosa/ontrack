import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";

export const useWorkflowInstanceStatus = () => {

    const {data, loading} = useQuery(
        gql`
            query WorkflowInstanceStatusQuery {
                workflowInstanceStatusList
            }
        `,
        {
            initialData: [],
            dataFn: data => data.workflowInstanceStatusList,
        }
    )

    return {
        data,
        loading,
    }

}