import {Space} from "antd";
import LoadMoreButton from "@components/common/LoadMoreButton";

export default function TablePaginationFooter({pageInfo, setPagination, extra}) {

    const onLoadMore = () => {
        if (pageInfo.nextPage) {
            setPagination(pageInfo.nextPage)
        }
    }

    return (
        <>
            <Space>
                <LoadMoreButton
                    pageInfo={pageInfo}
                    moreText="There are more entries to be loaded"
                    noMoreText="There are no more entries to be loaded"
                    onLoadMore={onLoadMore}
                />
                {extra}
            </Space>
        </>
    )
}