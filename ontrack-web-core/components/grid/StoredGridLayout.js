import {useContext, useEffect, useState} from "react";
import GridTable from "@components/grid/GridTable";
import {StoredGridLayoutContext} from "@components/grid/StoredGridLayoutContext";
import GridTableContextProvider from "@components/grid/GridTableContext";

export default function StoredGridLayout({id, defaultLayout, items, rowHeight = 200}) {

    const [layout, setLayout] = useState(defaultLayout)
    const [loaded, setLoaded] = useState(false)

    useEffect(() => {
        const value = localStorage.getItem(id)
        if (value) {
            setLayout(JSON.parse(value))
        }
        setLoaded(true)
    }, []);

    const {resetLayoutCount} = useContext(StoredGridLayoutContext)
    useEffect(() => {
        if (resetLayoutCount > 0) {
            setLayout(defaultLayout)
        }
    }, [defaultLayout, resetLayoutCount]);

    const onLayoutChange = (newLayout) => {
        localStorage.setItem(id, JSON.stringify(newLayout))
    }

    return (
        <>
            {
                loaded &&
                <GridTableContextProvider>
                    <GridTable
                        rowHeight={rowHeight}
                        layout={layout}
                        onLayoutChange={onLayoutChange}
                        items={items}
                    />
                </GridTableContextProvider>
            }
        </>
    )
}