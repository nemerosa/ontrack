import BuildContentPromotions from "@components/builds/BuildContentPromotions";
import BuildContentValidations from "@components/builds/BuildContentValidations";
import BuildContentLinks from "@components/builds/BuildContentLinks";

import GridLayout from "@components/grid/GridLayout";

export default function BuildContent({build}) {

    const layout = [
        {i: "promotions", x: 0, y: 0, w: 4, h: 1},
        {i: "validations", x: 6, y: 0, w: 8, h: 3},
        {i: "links", x: 0, y: 1, w: 4, h: 2}
    ]

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
                initialLayout={layout}
                layoutKey="layoutBuildPage"
                items={items}
            />
        </>
    )
}