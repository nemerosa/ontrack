import {useContext} from "react";
import {WidgetExpansionContext} from "@components/dashboards/layouts/WidgetExpansionContext";
import DashboardWidget from "@components/dashboards/widgets/DashboardWidget";
import {LayoutContext} from "@components/dashboards/layouts/LayoutContext";

export default function LayoutContainer({loadedLayout}) {

    const {expansion} = useContext(WidgetExpansionContext)
    const widgets = useContext(LayoutContext)

    const selectedWidget = (uuid) => {
        return widgets.find(it => it.uuid === uuid)
    }

    return (
        <>
            {
                !expansion.uuid && loadedLayout
            }
            {
                expansion.uuid && <div style={{width: '100%'}}>
                    <DashboardWidget widget={selectedWidget(expansion.uuid)}/>
                </div>
            }
        </>
    )
}