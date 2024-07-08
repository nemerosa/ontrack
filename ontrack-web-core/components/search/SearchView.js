import {useRefData} from "@components/providers/RefDataProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeUri} from "@components/common/Links";
import {CloseCommand} from "@components/common/Commands";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import MainPage from "@components/layouts/MainPage";

export default function SearchView({q}) {
    const {searchResultTypes} = useRefData()
    const client = useGraphQLClient()

    // useEffect(() => {
    //     if (client && q) {
    //         client.request(
    //             gql`
    //                 query Search($q: String!) {
    //                     search()
    //                 }
    //             `,
    //             {q}
    //         )
    //     }
    // }, [client, q])

    return (
        <>
            <Head>
                {pageTitle("Search")}
            </Head>
            <MainPage
                title="Search"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            />
        </>
    )
}