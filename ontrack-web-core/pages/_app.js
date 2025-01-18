import 'antd/dist/reset.css'
import '@/styles/globals.css'
import 'react-grid-layout/css/styles.css'
import 'react-resizable/css/styles.css'
import 'reactflow/dist/style.css'
import UserContextProvider from "@components/providers/UserProvider";
import EventsContextProvider from "@components/common/EventsContext";
import PreferencesContextProvider from "@components/providers/PreferencesProvider";
import RefDataContextProvider from "@components/providers/RefDataProvider";
import ConnectionContextProvider from "@components/providers/ConnectionContextProvider";
import Head from "next/head";
import {useRouter} from "next/router";
import {isConnectionLoggingEnabled, isConnectionTracingEnabled, ontrackUiUrl, ontrackUrl} from "@/connection";
import SearchContextProvider from "@components/search/SearchContext";
// Ace editors modes & themes
import 'ace-builds/src-noconflict/ace';
import 'ace-builds/src-noconflict/mode-yaml';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-github';

export async function getServerSideProps() {
    console.log("[init] Environment ", ontrack)
    return {
        props: {
            environment: {
                ontrack,
            }
        }
    }
}

App.getInitialProps = async (ctx) => {
    const ontrack = {
        url: ontrackUrl(),
        ui: {
            url: ontrackUiUrl(),
        },
        connection: {
            logging: isConnectionLoggingEnabled(),
            tracing: isConnectionTracingEnabled(),
        }
    }
    const environment = {ontrack}
    return {
        environment,
    }
}

export default function App({Component, environment, pageProps}) {

    const router = useRouter()

    return (
        <>
            <Head>
                <link rel="shortcut icon" href={`${router.basePath}/favicon.ico`}/>
            </Head>
            <ConnectionContextProvider environment={environment}>
                <UserContextProvider>
                    <PreferencesContextProvider>
                        <RefDataContextProvider>
                            <SearchContextProvider>
                                <EventsContextProvider>
                                    <Component {...pageProps} />
                                </EventsContextProvider>
                            </SearchContextProvider>
                        </RefDataContextProvider>
                    </PreferencesContextProvider>
                </UserContextProvider>
            </ConnectionContextProvider>
        </>
    )
}
