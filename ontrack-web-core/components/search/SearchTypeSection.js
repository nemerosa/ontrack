import {Card, Skeleton} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import SearchResultList from "@components/search/SearchResultList";
import SearchResultType from "@components/search/SearchResultType";

SearchResultType.propTypes = {};
export default function SearchTypeSection({type, q}) {

    const client = useGraphQLClient()
    const [searching, setSearching] = useState(true)
    const [results, setResults] = useState([])

    useEffect(() => {
        if (client && q) {
            setSearching(true)
            client.request(
                gql`
                    query SearchWithType($type: String!, $q: String!) {
                        search(offset: 0, size: 10, type: $type, token: $q) {
                            pageInfo {
                                totalSize
                                nextPage {
                                    offset
                                    size
                                }
                            }
                            pageItems {
                                title
                                description
                                data
                            }
                        }
                    }
                `,
                {
                    type: type.id,
                    q: q,
                }
            ).then(data => {
                setResults(data.search.pageItems)
            }).finally(() => {
                setSearching(false)
            })
        }
    }, [client, q, type.id])

    return (
        <>
            {
                (searching || results.length > 0) &&
                <Card
                    title={
                        <SearchResultType type={type}/>
                    }
                    size="small"
                >
                    <Skeleton active loading={searching}>
                        <SearchResultList type={type} results={results}/>
                    </Skeleton>
                </Card>
            }
        </>
    )
}