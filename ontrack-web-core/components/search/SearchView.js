import {useRefData} from "@components/providers/RefDataProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeUri} from "@components/common/Links";
import {CloseCommand} from "@components/common/Commands";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import MainPage from "@components/layouts/MainPage";
import {Button, Popover, Skeleton, Space} from "antd";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import SearchResultList from "@components/search/SearchResultList";
import SearchResultType from "@components/search/SearchResultType";
import {FaBan} from "react-icons/fa";
import {useRouter} from "next/router";

export default function SearchView() {

    const router = useRouter()
    const {q, type} = router.query

    const client = useGraphQLClient()
    const [searching, setSearching] = useState(true)
    const [selectedType, setSelectedType] = useState(type)
    const [results, setResults] = useState([])

    const {searchResultTypes} = useRefData()

    useEffect(() => {
        if (client && q) {
            setSearching(true)
            client.request(
                gql`
                    query SearchWithType($type: String, $q: String!) {
                        search(offset: 0, size: 10, type: $type, token: $q) {
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
                                page
                            }
                        }
                    }
                `,
                {
                    q: q,
                    type: selectedType,
                }
            ).then(data => {
                setResults(data.search.pageItems)
            }).finally(() => {
                setSearching(false)
            })
        }
    }, [client, q, selectedType])

    const selectType = (type) => {
        setSelectedType(type)
        const query = {q}
        if (type) {
            query.type = type
        }
        router.replace({
            pathname: '/search',
            query: query
        }, undefined, {shallow: true})
    }

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
                    <Space direction="vertical" className="ot-line">
                        <Space direction="horizontal" wrap className="ot-line">
                            <Button
                                style={{height: 48}}
                                onClick={() => selectType('')}
                                icon={<FaBan/>}
                                type={selectedType === '' ? "primary" : "default"}
                            >
                                All types
                            </Button>
                            {
                                searchResultTypes.map(type => (
                                    <>
                                        <Popover
                                            key={type.id}
                                            title={`Filters results on ${type.name}`}
                                        >
                                            <Button
                                                style={{height: 48}}
                                                onClick={() => selectType(type.id)}
                                                type={selectedType === type.id ? "primary" : "default"}
                                            >
                                                <SearchResultType type={type} displayName={true} popover={false}/>
                                            </Button>
                                        </Popover>
                                    </>
                                ))
                            }
                        </Space>
                        <SearchResultList results={results}/>
                    </Space>
                </Skeleton>
            </MainPage>
        </>
    )
}