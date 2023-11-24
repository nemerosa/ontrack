import 'antd/dist/reset.css'
import '@/styles/globals.css'
import 'reactflow/dist/style.css'
import UserContextProvider from "@components/providers/UserProvider";
import EventsContextProvider from "@components/common/EventsContext";
import PreferencesContextProvider from "@components/providers/PreferencesProvider";
import RefDataContextProvider from "@components/providers/RefDataProvider";
import ConnectionContextProvider from "@components/providers/ConnectionContextProvider";

export default function App({Component, pageProps}) {
    return (
        <>
            <ConnectionContextProvider>
                <UserContextProvider>
                    <PreferencesContextProvider>
                        <RefDataContextProvider>
                            <EventsContextProvider>
                                <Component {...pageProps} />
                            </EventsContextProvider>
                        </RefDataContextProvider>
                    </PreferencesContextProvider>
                </UserContextProvider>
            </ConnectionContextProvider>
        </>
    )
}
