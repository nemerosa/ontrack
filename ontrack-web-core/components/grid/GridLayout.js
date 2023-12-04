import RGL, {WidthProvider} from "react-grid-layout";

import {GridCellWrapper} from "@components/grid/GridCellWrapper";
import {useContext, useEffect, useState} from "react";
import {GridLayoutContext} from "@components/grid/GridLayoutContextProvider";

const ReactGridLayout = WidthProvider(RGL);

export const useStoredLayout = (key, defaultLayout) => {
    const gridLayoutContext = useContext(GridLayoutContext)
    const [layout, setLayout] = useState(defaultLayout)

    useEffect(() => {
        const value = localStorage.getItem(key)
        if (value) {
            setLayout(JSON.parse(value))
        }
    }, [])

    useEffect(() => {
        if (gridLayoutContext.resetLayoutCount > 0) {
            setLayout(defaultLayout)
        }
    }, [gridLayoutContext.resetLayoutCount])

    return {
        layout,
        setLayout: (layout) => {
            localStorage.setItem(key, JSON.stringify(layout))
        }
    }
}

export default function GridLayout({
                                       layout,
                                       setLayout,
                                       isDraggable = false,
                                       isResizable = false,
                                       cols = 12,
                                       rowHeight = 200,
                                       compactType = 'vertical',
                                       items = {},
                                   }) {

    const onLayoutChange = (layout) => {
        if (setLayout) {
            setLayout(layout)
        }
    }

    return (
        <>
            <ReactGridLayout
                className="layout"
                layout={layout}
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
                    Object.keys(items).map(key => {
                        const item = items[key]
                        return (
                            <GridCellWrapper key={key}>
                                {item}
                            </GridCellWrapper>
                        )
                    })
                }
            </ReactGridLayout>
        </>
    )
}