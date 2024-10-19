import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import Link from "next/link";

export const slotUri = ({id}) => `/extension/environments/slot/${id}`

export const environmentsUri = `/extension/environments/environments`

export const environmentsBreadcrumbs = () => [
    ...homeBreadcrumbs(),
    <Link key="environments" href={environmentsUri}>Environments</Link>,
]
