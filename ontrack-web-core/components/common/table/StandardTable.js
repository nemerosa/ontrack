import {Space, Table} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import TablePaginationFooter from "@components/common/table/TablePaginationFooter";

export default function StandardTable({
                                          query,
                                          queryNode,
                                          variables = {},
                                          reloadCount = 0,
                                          columns = [],
                                          expandable = false,
                                          size = 10,
                                          filter = {},
                                          onFilterChange = (_) => {},
                                          footerExtra = '',
                                      }) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [items, setItems] = useState([])

    const [pagination, setPagination] = useState({
        offset: 0,
        size: size,
    })

    const [pageInfo, setPageInfo] = useState({})

    useEffect(() => {
        if (client && query && variables !== undefined) {
            setLoading(true)
            client.request(
                query,
                {
                    ...variables,
                    ...filter,
                    offset: pagination.offset,
                    size: pagination.size,
                }
            ).then(data => {
                const newItems = data[queryNode].pageItems;
                setPageInfo(data[queryNode].pageInfo)
                if (pagination.offset > 0) {
                    setItems((entries) => [...entries, ...newItems])
                } else {
                    setItems(newItems)
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, query, pagination, filter, reloadCount]);

    const onTableChange = (_, filters) => {
        if (onFilterChange) {
            onFilterChange(filters)
        }
    }

    return (
        <>
            <Table
                loading={loading}
                dataSource={items}
                pagination={false}
                columns={columns}
                expandable={expandable}
                onChange={onTableChange}
                footer={() =>
                    <Space>
                        <TablePaginationFooter
                            pageInfo={pageInfo}
                            setPagination={setPagination}
                        />
                        {footerExtra}
                    </Space>
                }
            >

            </Table>
        </>
    )
}