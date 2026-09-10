import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileScreenPending from "@components/mobile/MobileScreenPending"

/** The project list. A placeholder until the project screens land. */
export default function MobileProjectsPage() {
    return (
        <MobileScreen title="Projects">
            <MobileScreenPending desktopHref="/"/>
        </MobileScreen>
    )
}
