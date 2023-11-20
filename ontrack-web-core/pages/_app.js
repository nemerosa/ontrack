import 'antd/dist/reset.css'
import '@/styles/globals.css'
import 'reactflow/dist/style.css'
import UserContextProvider from "@components/providers/UserProvider";
import EventsContextProvider from "@components/common/EventsContext";
import PreferencesContextProvider from "@components/providers/PreferencesProvider";
import RefDataContextProvider from "@components/providers/RefDataProvider";

export default function App({Component, pageProps}) {
    return (
        <>
            <UserContextProvider>
                <PreferencesContextProvider>
                    <RefDataContextProvider>
                        <EventsContextProvider>
                            <Component {...pageProps} />
                        </EventsContextProvider>
                    </RefDataContextProvider>
                </PreferencesContextProvider>
            </UserContextProvider>
        </>
    )
}
