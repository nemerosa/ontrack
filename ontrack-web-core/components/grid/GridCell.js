import {Space, Spin, Typography} from "antd";
import {FaArrowsAlt, FaCompressArrowsAlt, FaExpandArrowsAlt} from "react-icons/fa";

import GridCellCommand from "@components/grid/GridCellCommand";
import Section from "@components/common/Section";
import {useContext, useState} from "react";
import {GridTableContext} from "@components/grid/GridTableContext";

export default function GridCell({
                                     id,
                                     title,
                                     titleWidth = 18,
                                     loading,
                                     padding = true,
                                     isDraggable = true,
                                     extra,
                                     children
                                 }) {

    const {expandable, expandedId, toggleExpandedId} = useContext(GridTableContext)

    const bodyStyle = {
        overflowY: 'scroll',
    }
    if (!padding) {
        bodyStyle.padding = 0
    }

    const toggleExpansion = () => {
        if (id) {
            toggleExpandedId(id)
        }
    }

    return (
        <>
            <Section
                title={loading ? "Loading..." : title}
                titleWidth={titleWidth}
                extra={
                    <Space>
                        {extra}
                        {expandable && id &&
                            <>
                                <GridCellCommand
                                    condition={expandedId !== id}
                                    title="Makes the widget full size"
                                    icon={<FaExpandArrowsAlt/>}
                                    onAction={toggleExpansion}
                                />
                                <GridCellCommand
                                    condition={expandedId === id}
                                    title="Makes the widget to its regular size"
                                    icon={<FaCompressArrowsAlt/>}
                                    onAction={toggleExpansion}
                                />
                            </>
                        }
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
