import {useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";
import {Space, Spin, Typography} from "antd";
import SearchResultType from "@components/search/SearchResultType";
import SearchResult from "@components/search/SearchResult";

export default function SearchBoxTypeResults({type, query, onSelect}) {
    const {data, loading, error} = useQuery(
        gql`
            query SearchByType($type: String!, $token: String!) {
                search(type: $type, token: $token, offset: 0, size: 5) {
                    pageInfo {
                        totalSize
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
            variables: {
                type: type.id,
                token: query,
            },
            condition: !!query && query.length >= 3,
            deps: [type.id, query],
        }
    )

    const items = data?.search?.pageItems || []
    const totalSize = data?.search?.pageInfo?.totalSize || 0

    return (
        <div style={{
            padding: '8px 12px'
        }}>
            <Space direction="vertical" style={{width: '100%'}}>
                <Space direction="horizontal">
                    <SearchResultType type={type} displayName={true} popover={false}/>
                    {loading && <Spin size="small"/>}
                    {!loading && !error && (
                        <Typography.Text type="secondary">
                            ({totalSize})
                        </Typography.Text>
                    )}
                </Space>
                {
                    loading && items.length === 0 && (
                        <Space>
                            <Spin size="small"/>
                            <Typography.Text type="secondary">Searching...</Typography.Text>
                        </Space>
                    )
                }
                {
                    !loading && items.length === 0 && !error && (
                        <Typography.Text type="secondary">No results</Typography.Text>
                    )
                }
                {
                    error && (
                        <Typography.Text type="danger">Search error</Typography.Text>
                    )
                }
                {
                    items.map((item, idx) => (
                        <SearchResult key={idx} result={item} showType={false} onSelect={onSelect}/>
                    ))
                }
            </Space>
        </div>
    )
}
