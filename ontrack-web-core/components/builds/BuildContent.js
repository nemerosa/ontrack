import BuildContentPromotions from "@components/builds/BuildContentPromotions";
import BuildContentValidations from "@components/builds/BuildContentValidations";
import BuildContentLinks from "@components/builds/BuildContentLinks";
import StoredGridLayout from "@components/grid/StoredGridLayout";

export default function BuildContent({build}) {

    const defaultLayout = [
        {i: "promotions", x: 0, y: 0, w: 4, h: 1},
        {i: "validations", x: 6, y: 0, w: 8, h: 3},
        {i: "links", x: 0, y: 1, w: 4, h: 2}
    ]

    const items = [
        {
            id: "promotions",
            content: <BuildContentPromotions
                build={build}
            />,
        },
        {
            id: "validations",
            content: <BuildContentValidations
                build={build}
            />,
        },
        {
            id: "links",
            content: <BuildContentLinks
                build={build}
            />,
        },
    ]

    return (
        <>
            <StoredGridLayout
                id="page-build-layout"
                defaultLayout={defaultLayout}
                items={items}
            />
        </>
    )
}