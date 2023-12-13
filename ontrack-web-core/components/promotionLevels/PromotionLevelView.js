import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import Head from "next/head";
import {promotionLevelTitle} from "@components/common/Titles";
import StoredGridLayoutContextProvider from "@components/grid/StoredGridLayoutContext";
import MainPage from "@components/layouts/MainPage";
import {Space} from "antd";
import {promotionLevelBreadcrumbs} from "@components/common/Breadcrumbs";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import LoadingContainer from "@components/common/LoadingContainer";
import StoredGridLayout from "@components/grid/StoredGridLayout";

export default function PromotionLevelView({id}) {

    const client = useGraphQLClient()

    const [loadingPromotionLevel, setLoadingPromotionLevel] = useState(true)
    const [promotionLevel, setPromotionLevel] = useState({branch: {project: {}}})
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (client && id) {
            setLoadingPromotionLevel(true)
            client.request(
                gql`
                    query LoadPromotionLevel($id: Int!) {
                        promotionLevel(id: $id) {
                            id
                            name
                            description
                            image
                            branch {
                                id
                                name
                                project {
                                    id
                                    name
                                }
                            }
                        }
                    }
                `,
                {id}
            ).then(data => {
                setPromotionLevel(data.promotionLevel)
                setCommands([])
            }).finally(() => {
                setLoadingPromotionLevel(false)
            })
        }
    }, [client, id]);

    const defaultLayout = [
        {i: "chart-lead-time", x: 0, y: 0, w: 6, h: 6},
    ]

    const items = [
        {
            id: "chart-lead-time",
            content: "TODO Chart lead time",
        },
    ]

    return (
        <>
            <Head>
                {promotionLevelTitle(promotionLevel)}
            </Head>
            <StoredGridLayoutContextProvider>
                <MainPage
                    title={
                        <Space>
                            <PromotionLevelImage promotionLevel={promotionLevel}/>
                            {promotionLevel.name}
                        </Space>
                    }
                    breadcrumbs={promotionLevelBreadcrumbs(promotionLevel)}
                    commands={commands}
                >
                    <LoadingContainer loading={loadingPromotionLevel} tip="Loading promotion level">
                        <StoredGridLayout
                            id="page-promotion-level-layout"
                            defaultLayout={defaultLayout}
                            items={items}
                            rowHeight={30}
                        />
                        {/* TODO Promotion levels properties */}
                    </LoadingContainer>
                </MainPage>
            </StoredGridLayoutContextProvider>
        </>
    )
}