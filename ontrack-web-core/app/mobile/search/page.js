import MobileScreen from "@components/mobile/layout/MobileScreen"
import MobileScreenPending from "@components/mobile/MobileScreenPending"

/** Search. A placeholder until the mobile search screen lands. */
export default function MobileSearchPage() {
    return (
        <MobileScreen title="Search">
            <MobileScreenPending desktopHref="/search"/>
        </MobileScreen>
    )
}
