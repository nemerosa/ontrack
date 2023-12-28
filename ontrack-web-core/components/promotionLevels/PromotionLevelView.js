import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useContext, useEffect, useState} from "react";
import Head from "next/head";
import {promotionLevelTitle} from "@components/common/Titles";
import StoredGridLayoutContextProvider from "@components/grid/StoredGridLayoutContext";
import MainPage from "@components/layouts/MainPage";
import {Space} from "antd";
import {promotionLevelBreadcrumbs} from "@components/common/Breadcrumbs";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import LoadingContainer from "@components/common/LoadingContainer";
import StoredGridLayout from "@components/grid/StoredGridLayout";
import PromotionLevelLeadTimeChart from "@components/promotionLevels/PromotionLevelLeadTimeChart";
import GridCell from "@components/grid/GridCell";
import PromotionLevelTTRChart from "@components/promotionLevels/PromotionLevelTTRChart";
import {useChartOptionsCommand} from "@components/charts/ChartOptionsDialog";
import StoredGridLayoutResetCommand from "@components/grid/StoredGridLayoutResetCommand";
import PromotionLevelChangeImageCommand from "@components/promotionLevels/PromotionLevelChangeImageCommand";
import PromotionLevelUpdateCommand from "@components/promotionLevels/PromotionLevelUpdateCommand";
import {useEventForRefresh} from "@components/common/EventsContext";
import PromotionLevelDeleteCommand from "@components/promotionLevels/PromotionLevelDeleteCommand";
import {getPromotionLevelById} from "@components/services/fragments";
import {isAuthorized} from "@components/common/authorizations";
import {UserContext} from "@components/providers/UserProvider";
import PromotionLevelBulkUpdateCommand from "@components/promotionLevels/PromotionLevelBulkUpdateCommand";

export default function PromotionLevelView({id}) {

    const chartLeadTime = "chart-lead-time"
    const chartTTR = "chart-ttr"

    const client = useGraphQLClient()

    const [loadingPromotionLevel, setLoadingPromotionLevel] = useState(true)
    const [promotionLevel, setPromotionLevel] = useState({branch: {project: {}}})
    const [commands, setCommands] = useState([])

    const refreshCount = useEventForRefresh("promotionLevel.updated")

    const user = useContext(UserContext)

    useEffect(() => {
        if (client && id) {
            setLoadingPromotionLevel(true)
            getPromotionLevelById(client, id).then(pl => {
                setPromotionLevel(pl)
                const commands = []
                if (isAuthorized(pl, 'promotion_level', 'edit')) {
                    commands.push(<PromotionLevelChangeImageCommand key="change-image" id={id}/>)
                    commands.push(<PromotionLevelUpdateCommand key="update" id={id}/>)
                }
                if (isAuthorized(pl, 'promotion_level', 'delete')) {
                    commands.push(<PromotionLevelDeleteCommand key="delete" id={id}/>)
                }
                if (user.authorizations?.promotion_level?.bulkUpdate) {
                    commands.push(<PromotionLevelBulkUpdateCommand key="bulk-update" id={id}/>)
                }
                commands.push(<StoredGridLayoutResetCommand key="reset"/>)
                setCommands(commands)
            }).finally(() => {
                setLoadingPromotionLevel(false)
            })
        }
    }, [client, id, refreshCount, user]);

    const defaultLayout = [
        {i: chartLeadTime, x: 0, y: 0, w: 6, h: 12},
        {i: chartTTR, x: 6, y: 0, w: 6, h: 12},
    ]

    const {command, interval, period} = useChartOptionsCommand()

    const items = [
        {
            id: chartLeadTime,
            content: <GridCell
                id={chartLeadTime}
                title="Lead time to promotion"
                extra={command}
            >
                <PromotionLevelLeadTimeChart
                    promotionLevel={promotionLevel}
                    interval={interval}
                    period={period}
                />
            </GridCell>,
        },
        {
            id: chartTTR,
            content: <GridCell
                id={chartTTR}
                title="Time to recovery to promotion"
                extra={command}
            >
                <PromotionLevelTTRChart
                    promotionLevel={promotionLevel}
                    interval={interval}
                    period={period}
                />
            </GridCell>,
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
                    description={promotionLevel.description}
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