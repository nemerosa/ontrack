import 'antd/dist/reset.css'
import '@/styles/globals.css'
import UserContextProvider from "@components/providers/UserProvider";
import EventsContextProvider from "@components/common/EventsContext";

export default function App({Component, pageProps}) {
    return (
        <>
            <UserContextProvider>
                <EventsContextProvider>
                    <Component {...pageProps} />
                </EventsContextProvider>
            </UserContextProvider>
        </>
    )
}
