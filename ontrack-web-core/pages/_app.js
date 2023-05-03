import 'antd/dist/reset.css'
import '@/styles/globals.css'
import UserContextProvider from "@components/providers/UserProvider";

export default function App({Component, pageProps}) {
    return (
        <>
            <UserContextProvider>
                <Component {...pageProps} />
            </UserContextProvider>
        </>
    )
}
