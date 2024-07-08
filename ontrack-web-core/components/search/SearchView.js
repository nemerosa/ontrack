import {useRefData} from "@components/providers/RefDataProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeUri} from "@components/common/Links";
import {CloseCommand} from "@components/common/Commands";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import MainPage from "@components/layouts/MainPage";
import {Skeleton} from "antd";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import SearchResultList from "@components/search/SearchResultList";

export default function SearchView({q}) {

    const client = useGraphQLClient()
    const [searching, setSearching] = useState(true)
    const [results, setResults] = useState([])

    useEffect(() => {
        if (client && q) {
            setSearching(true)
            client.request(
                gql`
                    query SearchWithType($q: String!) {
                        search(offset: 0, size: 10, token: $q) {
                            pageInfo {
                                totalSize
                                nextPage {
                                    offset
                                    size
                                }
                            }
                            pageItems {
                                type {
                                    id
                                    name
                                    description
                                }
                                title
                                description
                                data
                                accuracy
                            }
                        }
                    }
                `,
                {
                    q: q,
                }
            ).then(data => {
                setResults(data.search.pageItems)
            }).finally(() => {
                setSearching(false)
            })
        }
    }, [client, q])

    return (
        <>
            <Head>
                {pageTitle("Search")}
            </Head>
            <MainPage
                title="Search"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            >
                <Skeleton active loading={searching}>
                    <SearchResultList results={results}/>
                </Skeleton>
            </MainPage>
        </>
    )
}