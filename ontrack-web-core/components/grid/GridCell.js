import {Space, Spin, Typography} from "antd";
import {FaArrowsAlt} from "react-icons/fa";

import GridCellCommand from "@components/grid/GridCellCommand";
import Section from "@components/common/Section";

export default function GridCell({title, titleWidth = 18, loading, padding = true, isDraggable = true, extra, children}) {

    const bodyStyle = {
        overflowY: 'scroll',
    }
    if (!padding) {
        bodyStyle.padding = 0
    }

    return (
        <>
            <Section
                title={loading ? "Loading..." : title}
                titleWidth={titleWidth}
                extra={
                    <Space>
                        {extra}
                        <GridCellCommand
                            condition={isDraggable}
                            icon={<FaArrowsAlt/>}
                            title="Use this handle to drag the widget into another position"
                            className="ot-rgl-draggable-handle"
                        />
                    </Space>
                }
            >
                {
                    !loading && children
                }
                {
                    loading &&
                    <Space>
                        <Spin/>
                        <Typography.Text>Loading...</Typography.Text>
                    </Space>
                }
            </Section>
        </>
    )
}
