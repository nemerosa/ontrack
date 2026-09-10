import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileScreenPending from "@components/mobile/MobileScreenPending"

/**
 * The mobile home screen.
 *
 * A placeholder until the favourites and project list land - the shell, the
 * redirect and the way back are what this change is about, and shipping them
 * with a screen that lies about being finished would be worse than one that
 * says so.
 */
export default function MobileHomePage() {
    return (
        <MobileScreen title="Home">
            <MobileScreenPending desktopHref="/"/>
        </MobileScreen>
    )
}
