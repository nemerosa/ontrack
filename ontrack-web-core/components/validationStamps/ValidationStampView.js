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
import PromotionLevelChangeImageCommand from "@components/promotionLevels/PromotionLevelChangeImageCommand";
import PromotionLevelUpdateCommand from "@components/promotionLevels/PromotionLevelUpdateCommand";
import ValidationStampChangeImageCommand from "@components/validationStamps/ValidationStampChangeImageCommand";

export default function ValidationStampView({id}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [validationStamp, setValidationStamp] = useState({branch: {project: {}}})
    const [commands, setCommands] = useState([])

    const refreshCount = useEventForRefresh("validationStamp.updated")

    const user = useContext(UserContext)

    useEffect(() => {
        if (client && id) {
            setLoading(true)
            getValidationStampById(client, id).then(vs => {
                setValidationStamp(vs)

                const commands = []
                if (isAuthorized(vs, 'validation_stamp', 'edit')) {
                    commands.push(<ValidationStampChangeImageCommand key="change-image" id={id}/>)
                    // TODO commands.push(<PromotionLevelUpdateCommand key="update" id={id}/>)
                }
                commands.push(<StoredGridLayoutResetCommand key="reset"/>)
                commands.push(<CloseCommand key="close" href={branchUri(vs.branch)}/>)
                setCommands(commands)

            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, id, refreshCount, user]);

    const sectionHistory = "section-history"

    const defaultLayout = [
        // History - table
        {i: sectionHistory, x: 0, y: 0, w: 12, h: 12},
    ]

    const items = [
        {
            id: sectionHistory,
            content: <GridCell
                id={sectionHistory}
                title="Validation history"
            >
                <ValidationStampHistory
                    validationStamp={validationStamp}
                />
            </GridCell>,
        },
    ]

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