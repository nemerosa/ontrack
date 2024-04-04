import Head from "next/head";
import {useContext, useEffect, useState} from "react";
import {validationStampTitle} from "@components/common/Titles";
import StoredGridLayoutContextProvider from "@components/grid/StoredGridLayoutContext";
import MainPage from "@components/layouts/MainPage";
import {Skeleton, Space} from "antd";
import {validationStampBreadcrumbs} from "@components/common/Breadcrumbs";
import StoredGridLayout from "@components/grid/StoredGridLayout";
import {useEventForRefresh} from "@components/common/EventsContext";
import {UserContext} from "@components/providers/UserProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {getValidationStampById} from "@components/services/fragments";
import {CloseCommand} from "@components/common/Commands";
import {branchUri} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";
import GridCell from "@components/grid/GridCell";
import ValidationStampHistory from "@components/validationStamps/ValidationStampHistory";
import StoredGridLayoutResetCommand from "@components/grid/StoredGridLayoutResetCommand";
import InfoBox from "@components/common/InfoBox";
import ValidationDataType from "@components/framework/validation-data-type/ValidationDataType";
import {isAuthorized} from "@components/common/authorizations";
import ValidationStampChangeImageCommand from "@components/validationStamps/ValidationStampChangeImageCommand";
import ValidationStampUpdateCommand from "@components/validationStamps/ValidationStampUpdateCommand";
import ValidationStampDeleteCommand from "@components/validationStamps/ValidationStampDeleteCommand";
import ValidationStampBulkUpdateCommand from "@components/validationStamps/ValidationStampBulkUpdateCommand";
import {useChartOptionsCommand} from "@components/charts/ChartOptionsDialog";
import ValidationStampLeadTimeChart from "@components/validationStamps/ValidationStampLeadTimeChart";
import ValidationStampFrequencyChart from "@components/validationStamps/ValidationStampFrequencyChart";
import ValidationStampStabilityChart from "@components/validationStamps/ValidationStampStabilityChart";
import ValidationStampMetricsChart from "@components/validationStamps/ValidationStampMetricsChart";

export default function ValidationStampView({id}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [validationStamp, setValidationStamp] = useState({branch: {project: {}}})
    const [commands, setCommands] = useState([])

    const chartLeadTime = "chart-lead-time"
    const chartFrequency = "chart-frequency"
    const chartMetrics = "chart-metrics"
    const chartStability = "chart-stability"
    const sectionHistory = "section-history"

    const [defaultLayout, setDefaultLayout] = useState([])
    const [items, setItems] = useState([])

    const refreshCount = useEventForRefresh("validationStamp.updated")

    const user = useContext(UserContext)

    useEffect(() => {
        if (client && id && command) {
            setLoading(true)
            getValidationStampById(client, id).then(vs => {
                setValidationStamp(vs)

                const commands = []
                if (isAuthorized(vs, 'validation_stamp', 'edit')) {
                    commands.push(<ValidationStampChangeImageCommand key="change-image" id={id}/>)
                    commands.push(<ValidationStampUpdateCommand key="update" id={id}/>)
                }
                if (isAuthorized(vs, 'validation_stamp', 'delete')) {
                    commands.push(<ValidationStampDeleteCommand key="delete" id={id}/>)
                }
                if (user.authorizations?.validation_stamp?.bulkUpdate) {
                    commands.push(<ValidationStampBulkUpdateCommand key="bulk-update" id={id}/>)
                }
                commands.push(<StoredGridLayoutResetCommand key="reset"/>)
                commands.push(<CloseCommand key="close" href={branchUri(vs.branch)}/>)
                setCommands(commands)

                // Charts
                const metricsChart = vs.charts.find(it => it.id === 'validation-stamp-metrics')
                const layout = [
                    {i: sectionHistory, x: 0, y: 0, w: 12, h: 12},
                ]
                if (metricsChart) {
                    layout.push(
                        {i: chartLeadTime, x: 0, y: 12, w: 6, h: 12},
                        {i: chartFrequency, x: 6, y: 12, w: 6, h: 12},
                        {i: chartMetrics, x: 0, y: 24, w: 6, h: 12},
                        {i: chartStability, x: 6, y: 24, w: 6, h: 12},
                    )
                } else {
                    layout.push(
                        {i: chartLeadTime, x: 0, y: 12, w: 4, h: 12},
                        {i: chartFrequency, x: 4, y: 12, w: 4, h: 12},
                        {i: chartStability, x: 8, y: 24, w: 4, h: 12},
                    )
                }

                const items = [
                    {
                        id: sectionHistory,
                        content: <GridCell
                            id={sectionHistory}
                            title="Validation history"
                        >
                            <ValidationStampHistory
                                validationStamp={vs}
                            />
                        </GridCell>,
                    },
                    {
                        id: chartLeadTime,
                        content: <GridCell
                            id={chartLeadTime}
                            title="Lead time to validation"
                            extra={command}
                        >
                            <ValidationStampLeadTimeChart
                                validationStamp={vs}
                                interval={interval}
                                period={period}
                            />
                        </GridCell>,
                    },
                    {
                        id: chartFrequency,
                        content: <GridCell
                            id={chartFrequency}
                            title="Frequency to validation"
                            extra={command}
                        >
                            <ValidationStampFrequencyChart
                                validationStamp={vs}
                                interval={interval}
                                period={period}
                            />
                        </GridCell>,
                    },
                    {
                        id: chartStability,
                        content: <GridCell
                            id={chartStability}
                            title="Stability of validation"
                            extra={command}
                        >
                            <ValidationStampStabilityChart
                                validationStamp={vs}
                                interval={interval}
                                period={period}
                            />
                        </GridCell>,
                    },
                ]

                if (metricsChart) {
                    items.push({
                        id: chartMetrics,
                        content: <GridCell
                            id={chartMetrics}
                            title="Validation metrtics"
                            extra={command}
                        >
                            <ValidationStampMetricsChart
                                validationStamp={vs}
                                interval={interval}
                                period={period}
                            />
                        </GridCell>,
                    })
                }

                setDefaultLayout(layout)
                setItems(items)

            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id, refreshCount, user]);

    const {command, interval, period} = useChartOptionsCommand()

    return (
        <>
            <Head>
                {validationStampTitle(validationStamp)}
            </Head>
            <StoredGridLayoutContextProvider>
                <MainPage
                    title={
                        <Space>
                            <ValidationStampImage validationStamp={validationStamp}/>
                            {validationStamp.name}
                        </Space>
                    }
                    breadcrumbs={validationStampBreadcrumbs(validationStamp)}
                    commands={commands}
                    description={
                        <Space direction="vertical">
                            {validationStamp.description}
                            {/* Validation stamp data config */}
                            {
                                validationStamp && validationStamp.dataType &&
                                <InfoBox>
                                    <ValidationDataType dataType={validationStamp.dataType}/>
                                </InfoBox>
                            }
                        </Space>
                    }
                >
                    <Skeleton loading={loading} active>
                        <StoredGridLayout
                            id="page-validation-stamp-layout"
                            defaultLayout={defaultLayout}
                            items={items}
                            rowHeight={30}
                        />
                        {/* TODO Validation stamps properties */}
                    </Skeleton>
                </MainPage>
            </StoredGridLayoutContextProvider>
        </>
    )
}