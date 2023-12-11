import RGL, {WidthProvider} from "react-grid-layout";
import {GridCellWrapper} from "@components/grid/GridCellWrapper";
import {useContext, useEffect, useState} from "react";
import {GridTableContext} from "@components/grid/GridTableContext";

const ReactGridLayout = WidthProvider(RGL);

export default function GridTable({
                                      layout,
                                      onLayoutChange,
                                      items,
                                      cols = 12,
                                      rowHeight = 200,
                                      defaultNewHeight = 4,
                                      compactType = 'vertical',
                                      isDraggable = true,
                                      isResizable = true,
                                  }) {

    const [actualLayout, setActualLayout] = useState(layout)
    const [actualItems, setActualItems] = useState(items)

    useEffect(() => {
        setActualLayout(layout)
    }, [layout]);

    useEffect(() => {
        setActualItems(items)
    }, [items]);

    const {expandedId} = useContext(GridTableContext)

    const maxYH = () => {
        let maxYH = 0
        layout.forEach(it => {
            maxYH = Math.max(maxYH, it.y + it.h)
        })
        return maxYH
    }

    useEffect(() => {
        if (expandedId) {
            const item = items.find(it => it.id === expandedId)
            if (item) {
                setActualLayout([{
                    i: item.id,
                    x: 0,
                    y: 0,
                    w: cols,
                    h: Math.max(maxYH(), defaultNewHeight * 2),
                }])
                setActualItems([item])
            }
        } else {
            setActualItems(items)
            setActualLayout(layout)
        }
    }, [expandedId]);

    return (
        <>
            <ReactGridLayout
                className="layout"
                layout={actualLayout}
                cols={cols}
                rowHeight={rowHeight}
                width="100%"
                compactType={compactType}
                margin={[8, 8]}
                containerPadding={[0, 8]}
                isDraggable={isDraggable}
                isResizable={isResizable}
                isBounded={false}
                draggableHandle='.ot-rgl-draggable-handle'
                onLayoutChange={onLayoutChange}
            >
                {
                    actualItems.map(item => (
                        <GridCellWrapper key={item.id}>
                            {item.content}
                        </GridCellWrapper>
                    ))
                }
            </ReactGridLayout>
        </>
    )
}