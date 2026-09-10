import MobileBuildScreen from "./BuildScreen"

/**
 * One build on a phone, reached from its branch or by following a desktop
 * `/build/[id]` link - which the redirect maps here.
 *
 * The screen itself is a client component; the page is only the route.
 */
export default function MobileBuildPage({params}) {
    return <MobileBuildScreen id={params.id}/>
}
