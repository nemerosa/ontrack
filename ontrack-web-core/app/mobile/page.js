import MobileHomeScreen from "./HomeScreen"

/**
 * The mobile home screen: the current user's favourite projects and branches.
 *
 * The screen itself is a client component - it reads the favourites and lets the
 * user unstar them - so the page is only the route.
 */
export default function MobileHomePage() {
    return <MobileHomeScreen/>
}
