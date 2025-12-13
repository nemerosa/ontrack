import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";
import {PreferencesContext} from "@components/providers/PreferencesProvider";
import {RefDataContext} from "@components/providers/RefDataProvider";
import LoadingLogo from "@components/providers/LoadingLogo";

export default function LoadingAggregator({children}) {
    const {name} = useContext(UserContext)
    const {loaded: preferencesLoaded} = useContext(PreferencesContext)
    const {loaded: refDataLoaded} = useContext(RefDataContext)

    const loaded = name && preferencesLoaded && refDataLoaded

    return loaded ? children : <LoadingLogo/>
}