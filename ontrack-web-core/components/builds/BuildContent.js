import BuildContentPromotions from "@components/builds/BuildContentPromotions";
import BuildContentValidations from "@components/builds/BuildContentValidations";
import BuildContentLinks from "@components/builds/BuildContentLinks";

import GridLayout, {useStoredLayout} from "@components/grid/GridLayout";

export default function BuildContent({build}) {

    const defaultLayout = [
        {i: "promotions", x: 0, y: 0, w: 4, h: 1},
        {i: "validations", x: 6, y: 0, w: 8, h: 3},
        {i: "links", x: 0, y: 1, w: 4, h: 2}
    ]

    const {layout, setLayout} = useStoredLayout("page-build-layout", defaultLayout)

    const items = {
        promotions: <BuildContentPromotions
            build={build}
        />,
        validations: <BuildContentValidations
            build={build}
        />,
        links: <BuildContentLinks
            build={build}
        />,
    }

    return (
        <>
            <GridLayout
                layout={layout}
                setLayout={setLayout}
                layoutKey="layoutBuildPage"
                items={items}
                isDraggable={true}
                isResizable={true}
            />
        </>
    )
}