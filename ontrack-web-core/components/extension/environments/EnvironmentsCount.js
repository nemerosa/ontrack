import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import LoadingInline from "@components/common/LoadingInline";

export default function EnvironmentsCount() {
    const {data: count, loading} = useQuery(
        gql`
            query EnvironmentCount {
                environmentsCount
            }
        `,
        {
            initialData: 0,
            dataFn: data => data.environmentsCount,
        }
    )
    return <LoadingInline loading={loading}>{count}</LoadingInline>
}