import {Button, Popover, Space, Typography} from "antd";
import {FaSearch} from "react-icons/fa";

export default function LoadMoreButton({
                                           pageInfo,
                                           text = "Load more...",
                                           moreText = "There are more items to be loaded",
                                           noMoreText = "There are no more items to be loaded",
                                           onLoadMore = () => {
                                           },
                                       }) {
    return (
        <>
            <Popover
                content={
                    (pageInfo && pageInfo.nextPage) ? moreText : noMoreText
                }
            >
                <Button
                    onClick={onLoadMore}
                    disabled={!pageInfo || !pageInfo.nextPage}
                >
                    <Space>
                        <FaSearch/>
                        <Typography.Text>{text}</Typography.Text>
                    </Space>
                </Button>
            </Popover>
        </>
    )
}