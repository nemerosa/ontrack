import BuildContentValidations from "@components/builds/BuildContentValidations";
import BuildContentLinksUsing from "@components/builds/BuildContentLinksUsing";
import StoredGridLayout from "@components/grid/StoredGridLayout";
import BuildContentLinksUsedBy from "@components/builds/BuildContentLinksUsedBy";
import BuildContentNotifications from "@components/builds/BuildContentNotifications";
import BuildContentPromotions from "@components/builds/BuildContentPromotions";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";
import BuildContentEnvironments from "@components/builds/BuildContentEnvironments";

export default function BuildContent({build}) {

    const user = useContext(UserContext)

    let defaultLayout = []
    let layoutName = ''
    if (user.authorizations.environment?.view) {
        defaultLayout = [
            {i: "promotions", x: 0, y: 0, w: 6, h: 9},
            {i: "environments", x: 6, y: 0, w: 6, h: 9},
            {i: "validations", x: 0, y: 9, w: 12, h: 9},
            {i: "using", x: 0, y: 18, w: 6, h: 9},
            {i: "usedBy", x: 6, y: 18, w: 6, h: 9},
            {i: "notifications", x: 0, y: 27, w: 12, h: 9},
        ]
        layoutName = "page-build-layout-v5-env"
    } else {
        defaultLayout = [
            {i: "promotions", x: 0, y: 0, w: 4, h: 9},
            {i: "validations", x: 6, y: 0, w: 8, h: 9},
            {i: "using", x: 0, y: 9, w: 6, h: 9},
            {i: "usedBy", x: 6, y: 9, w: 6, h: 9},
            {i: "notifications", x: 0, y: 18, w: 12, h: 9},
        ]
        layoutName = "page-build-layout-v5-noenv"
    }

    const items = [
        {
            id: "promotions",
            content: <BuildContentPromotions
                build={build}
            />,
        },
        {
            id: "environments",
            content: <BuildContentEnvironments
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
                id={layoutName}
                defaultLayout={defaultLayout}
                items={items}
                rowHeight={30}
            />
        </>
    )
}