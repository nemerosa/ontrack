import 'graphiql/graphiql.css';
import MainLayout from "@components/layouts/MainLayout";
import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import GraphiQLView from "@components/core/GraphiQLView";

export default function GraphiQLPage() {

    return (
        <>
            <main>
                <MainLayout>
                    <Head>
                        {pageTitle("GraphiQL")}
                    </Head>
                    <GraphiQLView/>
                </MainLayout>
            </main>
        </>
    )
}