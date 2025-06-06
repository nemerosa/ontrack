import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import {GraphiQL} from "graphiql";
import {useMemo} from "react";

export default function GraphiQLView() {

    const fetcher = useMemo(() => async (graphQLParams) => {
        const res = await fetch('/api/protected/graphql', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(graphQLParams),
        })
        const json = await res.json()
        return {data: json}
    }, [])

    return (
        <>
            <MainPage
                title="GraphiQL"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            >
                <div style={{width: '100%', height: '80vh'}}>
                    <GraphiQL
                        fetcher={fetcher}
                    />
                </div>
            </MainPage>
        </>
    )
}