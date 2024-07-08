import {useRefData} from "@components/providers/RefDataProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeUri} from "@components/common/Links";
import {CloseCommand} from "@components/common/Commands";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import MainPage from "@components/layouts/MainPage";
import {Space} from "antd";
import SearchTypeSection from "@components/search/SearchTypeSection";

export default function SearchView({q}) {
    const {searchResultTypes} = useRefData()

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
            >
                <Space direction="vertical" className="ot-line">
                    {
                        searchResultTypes.map(type => (
                            <>
                                <SearchTypeSection key={type.name} type={type} q={q}/>
                            </>
                        ))
                    }
                </Space>
            </MainPage>
        </>
    )
}