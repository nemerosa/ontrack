import RGL, {WidthProvider} from "react-grid-layout";

import {GridCellWrapper} from "@components/grid/GridCellWrapper";
import {useContext, useEffect, useState} from "react";
import {GridLayoutContext} from "@components/grid/GridLayoutContextProvider";

const ReactGridLayout = WidthProvider(RGL);

export default function GridLayout({
                                       initialLayout,
                                       layoutKey,
                                       cols = 12,
                                       rowHeight = 200,
                                       compactType = 'vertical',
                                       items = {},
                                   }) {

    let startupLayout = initialLayout
    if (layoutKey) {
        const value = localStorage.getItem(layoutKey)
        if (value) {
            startupLayout = JSON.parse(value)
        }
    }

    const [layout, setLayout] = useState(startupLayout)

    const onLayoutChange = (layout) => {
        if (layoutKey) {
            localStorage.setItem(layoutKey, JSON.stringify(layout))
        }
    }

    const context = useContext(GridLayoutContext)

    useEffect(() => {
        if (context.resetLayoutCount) {
            setLayout(initialLayout)
        }
    }, [context.resetLayoutCount]);

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