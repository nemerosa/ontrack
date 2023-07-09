import {Card, Tooltip, Typography} from "antd";
import {lazy, useEffect, useState} from "react";
import {FaCheck, FaCheckSquare, FaPlus} from "react-icons/fa";

export default function SelectableLayout({layoutDef, selected, onLayoutKeySelected}) {

    const onSelect = () => {
        if (onLayoutKeySelected) onLayoutKeySelected(layoutDef.key)
    }

    const importLayoutPreview = layoutKey => lazy(() =>
        import(`./${layoutKey}LayoutPreview`)
    )

    const [loadedLayoutPreview, setLoadedLayoutPreview] = useState(undefined)

    useEffect(() => {
        if (layoutDef) {
            const loadLayoutPreview = async () => {
                const LayoutPreview = await importLayoutPreview(layoutDef.key)
                setLoadedLayoutPreview(<LayoutPreview/>)
            }
            loadLayoutPreview().then(() => {
            })
        }
    }, [layoutDef])

    return (
        <>
            <Card
                title={
                    <Tooltip title={layoutDef.description}>
                        <Typography.Text>{layoutDef.name}</Typography.Text>
                    </Tooltip>
                }
                extra={
                    <Tooltip title={`Use the "${layoutDef.name}" layout for the dashboard`}>
                        {
                            selected ? <FaCheckSquare color="green"/> : <FaCheck/>
                        }
                    </Tooltip>
                }
                onClick={onSelect}
            >
                {loadedLayoutPreview}
            </Card>
        </>
    )
}