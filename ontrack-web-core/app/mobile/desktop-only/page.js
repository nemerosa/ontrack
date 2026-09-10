import {resolveInterstitialTarget} from "@components/mobile/mobileRedirect"
import DesktopOnlyScreen from "./DesktopOnlyScreen"

/**
 * Where the middleware sends a phone whose destination has no mobile equivalent.
 *
 * A server component so the `target` parameter is sanitised before it ever
 * reaches the browser: it ends up in a navigation, and anyone can type one.
 */
export default function DesktopOnlyPage({searchParams}) {
    return <DesktopOnlyScreen target={resolveInterstitialTarget(searchParams?.target)}/>
}
