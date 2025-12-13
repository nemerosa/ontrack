import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";

export const usePromotionLevelBranch = ({promotionLevelId}) => {
    const {data: branch, loading} = useQuery(
        gql`
            query PromotionDependenciesPropertyBranch(
                $promotionLevelId: Int!
            ) {
                promotionLevel(id: $promotionLevelId) {
                    branch {
                        id
                        name
                        project {
                            id
                            name
                        }
                    }
                }
            }
        `,
        {
            initialData: [],
            variables: {
                promotionLevelId: Number(promotionLevelId),
            },
            dataFn: data => data.promotionLevel?.branch,
        }
    )
    return {branch, loading}
}