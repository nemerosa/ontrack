import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import Link from "next/link";

export const slotPipelineUri = (id) => `/extension/environments/pipeline/${id}`

export const slotTitle = (slot, omitSlot = false) => {
    let title = `Slot ${slot.environment.name} - ${slot.project.name}`
    if (slot.qualifier) {
        title += ` [${slot.qualifier}]`
    }
    return title
}

export const slotUri = ({id}) => `/extension/environments/slot/${id}`

export const environmentsUri = `/extension/environments/environments`

export const restEnvironmentImageUri = ({id}) => `/rest/extension/environments/environments/${id}/image`

export const environmentsBreadcrumbs = () => [
    ...homeBreadcrumbs(),
    <Link key="environments" href={environmentsUri}>Environments</Link>,
]

export const slotBreadcrumbs = (slot) => [
    ...environmentsBreadcrumbs(),
    <Link key="slot" href={slotUri(slot)}>{slotTitle(slot)}</Link>,
]
