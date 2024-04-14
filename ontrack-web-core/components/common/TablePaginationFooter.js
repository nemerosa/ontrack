import {Button, Popover, Space, Typography} from "antd";
import {FaSearch} from "react-icons/fa";

export default function TablePaginationFooter({pageInfo, setPagination, extra}) {

    const onLoadMore = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    return (
        <>
            <Space>
                <Popover
                    content={
                        (pageInfo && pageInfo.nextPage) ?
                            "There are more entries to be loaded" :
                            "There are no more entries to be loaded"
                    }
                >
                    <Button
                        onClick={onLoadMore}
                        disabled={!pageInfo || !pageInfo.nextPage}
                    >
                        <Space>
                            <FaSearch/>
                            <Typography.Text>Load more...</Typography.Text>
                        </Space>
                    </Button>
                </Popover>
                {extra}
            </Space>
        </>
    )
}