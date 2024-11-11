import BuildContentValidations from "@components/builds/BuildContentValidations";
import BuildContentLinksUsing from "@components/builds/BuildContentLinksUsing";
import StoredGridLayout from "@components/grid/StoredGridLayout";
import BuildContentLinksUsedBy from "@components/builds/BuildContentLinksUsedBy";
import BuildContentNotifications from "@components/builds/BuildContentNotifications";
import BuildPromotionInfo from "@components/builds/BuildPromotionInfo";

export default function BuildContent({build}) {

    const defaultLayout = [
        {i: "promotions", x: 0, y: 0, w: 4, h: 18},
        {i: "validations", x: 6, y: 0, w: 8, h: 9},
        {i: "using", x: 4, y: 9, w: 4, h: 9},
        {i: "usedBy", x: 8, y: 9, w: 4, h: 9},
        {i: "notifications", x: 0, y: 18, w: 12, h: 9},
    ]

    const items = [
        {
            id: "promotions",
            content: <BuildPromotionInfo
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
            id: "using",
            content: <BuildContentLinksUsing
                build={build}
            />,
        },
        {
            id: "usedBy",
            content: <BuildContentLinksUsedBy
                build={build}
            />,
        },
        {
            id: "notifications",
            content: <BuildContentNotifications
                build={build}
            />,
        },
    ]

    return (
        <>
            <StoredGridLayout
                id="page-build-layout-v2"
                defaultLayout={defaultLayout}
                items={items}
                rowHeight={30}
            />
        </>
    )
}