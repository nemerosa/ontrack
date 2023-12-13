import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect} from "react";
import {gql} from "graphql-request";

export default function PromotionLevelLeadTimeChart({promotionLevel}) {

    const client = useGraphQLClient()

    useEffect(() => {
        if (client && promotionLevel) {
            client.request(
                gql`
                    query PromotionLevelLeadTimeChart($parameters: JSON!) {
                        getChart(input: {
                            name: "promotion-level-lead-time",
                            options: {
                                interval: "1y",
                                period: "1w",
                            },
                            parameters: $parameters,
                        })
                    }
                `,
                {
                    parameters: {
                        id: promotionLevel.id,
                    }
                }
            )
        }
    }, [client, promotionLevel]);

    return (
        <>
            TODO
        </>
    )
}