import MobileBranchScreen from "./BranchScreen"

/**
 * One branch on a phone, reached from a project screen, from a favourite, or by
 * following a desktop `/branch/[id]` link - which the redirect maps here.
 *
 * The screen itself is a client component; the page is only the route.
 */
export default function MobileBranchPage({params}) {
    return <MobileBranchScreen id={params.id}/>
}
